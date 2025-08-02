// 1. Main Application
package com.example.courtapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CourtAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourtAppApplication.class, args);
    }
}

// 2. Model
package com.example.courtapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class CaseDetail {
    @Id @GeneratedValue
    private Long id;
    private String caseType;
    private String caseNumber;
    private String filingYear;
    private String parties;
    private String hearingDate;
    private String judgmentLink;
    private LocalDateTime timestamp = LocalDateTime.now();

    // Getters and Setters
}

// 3. Repository
package com.example.courtapp.repository;

import com.example.courtapp.model.CaseDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseDetailRepository extends JpaRepository<CaseDetail, Long> {
}

// 4. Controller
package com.example.courtapp.controller;

import com.example.courtapp.model.CaseDetail;
import com.example.courtapp.repository.CaseDetailRepository;
import com.example.courtapp.scraper.CourtScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CaseController {

    @Autowired
    private CaseDetailRepository repository;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/search")
    public String search(@RequestParam String caseType,
                         @RequestParam String caseNumber,
                         @RequestParam String filingYear,
                         Model model) {
        try {
            CaseDetail detail = CourtScraper.scrape(caseType, caseNumber, filingYear);
            repository.save(detail);
            model.addAttribute("data", detail);
            return "result";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}

// 5. Scraper (Selenium Mock)
package com.example.courtapp.scraper;

import com.example.courtapp.model.CaseDetail;

public class CourtScraper {
    public static CaseDetail scrape(String type, String number, String year) {
        CaseDetail detail = new CaseDetail();
        detail.setCaseType(type);
        detail.setCaseNumber(number);
        detail.setFilingYear(year);
        detail.setParties("A vs B");
        detail.setHearingDate("2025-08-01");
        detail.setJudgmentLink("https://example.com/judgment.pdf");
        return detail;
    }
}

// 6. Templates (src/main/resources/templates)

// index.html
<!--
<form action="/search" method="post">
    <label>Case Type:</label><input name="caseType" />
    <label>Case Number:</label><input name="caseNumber" />
    <label>Filing Year:</label><input name="filingYear" />
    <button type="submit">Search</button>
</form>
-->

// result.html
<!--
<h2>Case Details</h2>
<p>Parties: <span th:text="${data.parties}"></span></p>
<p>Next Hearing: <span th:text="${data.hearingDate}"></span></p>
<a th:href="${data.judgmentLink}" target="_blank">Download PDF</a>
-->

// error.html
<!--
<h3>Error: <span th:text="${error}"></span></h3>
<a href="/">Back</a>
-->

// 7. Application Properties (src/main/resources/application.properties)
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
