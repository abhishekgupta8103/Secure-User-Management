package com.example.demo.service;

import com.example.demo.dto.MonthlyUserReportResponse;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private final UserRepository userRepository;

    public ReportService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<MonthlyUserReportResponse> getMonthlyUserReport() {

        List<Object[]> results = userRepository.getMonthlyUserReport();

        List<MonthlyUserReportResponse> reports = new ArrayList<>();

        for (Object[] row : results) {

            String month = (String) row[0];

            long totalUsers = ((Number) row[1]).longValue();

            reports.add(
                    new MonthlyUserReportResponse(
                            month,
                            totalUsers
                    )
            );
        }

        return reports;
    }
}