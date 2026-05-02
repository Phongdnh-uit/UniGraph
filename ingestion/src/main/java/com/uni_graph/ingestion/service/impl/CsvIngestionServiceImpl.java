package com.uni_graph.ingestion.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.uni_graph.ingestion.domain.*;
import com.uni_graph.ingestion.enums.LogicType;
import com.uni_graph.ingestion.enums.RuleType;
import com.uni_graph.ingestion.repository.*;
import com.uni_graph.ingestion.service.CsvIngestionService;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvIngestionServiceImpl implements CsvIngestionService {

  private final CourseRepository courseRepository;
  private final DepartmentRepository departmentRepository;
  private final RequirementRuleRepository ruleRepository;

  @Override
  @Transactional
  public void ingestCoursesFromCsv(InputStream inputStream) {
    log.info("Starting ingestion from CSV stream");
    Path tempFile = null;
    try {
      // Save InputStream to temp file for two-pass reading
      tempFile = Files.createTempFile("ingestion-", ".csv");
      Files.copy(inputStream, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

      // Pass 1: Create all Course and Department nodes
      createBaseNodes(tempFile.toFile());

      // Pass 2: Establish relationships
      linkRelationships(tempFile.toFile());

      log.info("Ingestion completed successfully.");
    } catch (IOException e) {
      log.error("Failed to process CSV stream", e);
      throw new RuntimeException("CSV processing failed", e);
    } finally {
      if (tempFile != null) {
        try {
          Files.deleteIfExists(tempFile);
        } catch (IOException e) {
          log.warn("Failed to delete temp file: {}", tempFile);
        }
      }
    }
  }

  private void createBaseNodes(File file) {
    log.info("Pass 1: Creating base nodes");
    Map<String, Department> departmentCache = new HashMap<>();
    try (CSVReader reader = new CSVReaderBuilder(new FileReader(file)).withSkipLines(1).build()) {
      String[] line;
      while ((line = reader.readNext()) != null) {
        if (line.length < 13) continue;

        String code = line[1].trim();
        if (code.isEmpty()) continue;

        String departmentName = line[5].trim();
        Department department = null;
        if (!departmentName.isEmpty()) {
          department =
              departmentCache.computeIfAbsent(
                  departmentName,
                  k -> {
                    Department d = new Department();
                    d.setName(k);
                    return departmentRepository.save(d);
                  });
        }

        Course course = courseRepository.findById(code).orElse(new Course());
        course.setCode(code);
        course.setTitleVn(line[2].trim());
        course.setTitleEn(line[3].trim());
        course.setStatus(line[4].trim());
        course.setCourseType(line[6].trim());
        course.setOldCode(line[7].trim());

        try {
          course.setTheoryCredits(Integer.parseInt(line[11].trim()));
          course.setPracticeCredits(Integer.parseInt(line[12].trim()));
        } catch (NumberFormatException e) {
          course.setTheoryCredits(0);
          course.setPracticeCredits(0);
        }

        course.setDepartment(department);
        courseRepository.save(course);
      }
    } catch (IOException | CsvValidationException e) {
      log.error("Error in Pass 1", e);
      throw new RuntimeException("Pass 1 failed", e);
    }
  }

  private void linkRelationships(File file) {
    log.info("Pass 2: Linking relationships");
    try (CSVReader reader = new CSVReaderBuilder(new FileReader(file)).withSkipLines(1).build()) {
      String[] line;
      while ((line = reader.readNext()) != null) {
        if (line.length < 13) continue;

        String code = line[1].trim();
        if (code.isEmpty()) continue;

        Course course = courseRepository.findById(code).orElseThrow();
        course.setEquivalentCourses(new ArrayList<>());
        course.setRequirementRules(new ArrayList<>());

        // 1. Equivalent Courses
        parseCodes(line[8])
            .forEach(
                eqCode -> {
                  courseRepository
                      .findById(eqCode)
                      .ifPresent(
                          eqCourse -> {
                            course.getEquivalentCourses().add(eqCourse);
                          });
                });

        // 2. Prerequisite Rules
        List<String> preCodes = parseCodes(line[9]);
        if (!preCodes.isEmpty()) {
          RequirementRule rule = new RequirementRule();
          rule.setRuleType(RuleType.PREREQUISITE);
          rule.setLogicType(LogicType.AND); // Default to AND
          preCodes.forEach(
              pCode -> {
                courseRepository
                    .findById(pCode)
                    .ifPresent(
                        pCourse -> {
                          rule.getSatisfiedByCourses().add(pCourse);
                        });
              });
          if (!rule.getSatisfiedByCourses().isEmpty()) {
            ruleRepository.save(rule);
            course.getRequirementRules().add(rule);
          }
        }

        // 3. Previous Rules
        List<String> prevCodes = parseCodes(line[10]);
        if (!prevCodes.isEmpty()) {
          RequirementRule rule = new RequirementRule();
          rule.setRuleType(RuleType.PREVIOUS);
          rule.setLogicType(LogicType.AND); // Default to AND
          prevCodes.forEach(
              pCode -> {
                courseRepository
                    .findById(pCode)
                    .ifPresent(
                        pCourse -> {
                          rule.getSatisfiedByCourses().add(pCourse);
                        });
              });
          if (!rule.getSatisfiedByCourses().isEmpty()) {
            ruleRepository.save(rule);
            course.getRequirementRules().add(rule);
          }
        }

        courseRepository.save(course);
      }
    } catch (IOException | CsvValidationException e) {
      log.error("Error in Pass 2", e);
      throw new RuntimeException("Pass 2 failed", e);
    }
  }

  private List<String> parseCodes(String raw) {
    if (raw == null || raw.trim().isEmpty()) return Collections.emptyList();
    return Arrays.stream(raw.split("[\\n\\r,;]+"))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toList());
  }
}
