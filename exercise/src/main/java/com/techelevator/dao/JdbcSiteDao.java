package com.techelevator.dao;

import com.techelevator.model.Site;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcSiteDao implements SiteDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcSiteDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Site> getSitesThatAllowRVs(int parkId) {
        List<Site> sites = new ArrayList<>();
        String sql = "SELECT p.park_id, s.site_id, s.campground_id, s.site_number, s.max_occupancy, s.accessible, s.max_rv_length, s.utilities " +
                "FROM site s " +
                "JOIN campground c ON c.campground_id = s.campground_id " +
                "JOIN park p ON p.park_id = c.park_id " +
                "WHERE p.park_id = ? AND s.max_rv_length > 0";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);
        while(results.next()){
            Site site = mapRowToSite(results);
            sites.add(site);
        }
        return sites;
    }

    @Override
    public List<Site> getAvailableSites(int parkId){
        List<Site> sites = new ArrayList<>();
        String sql = "SELECT s.site_id, s.campground_id, s.site_number, s.max_occupancy, s.accessible, s.max_rv_length, s.utilities " +
                "FROM site s " +
                "LEFT JOIN reservation r ON r.site_id = s.site_id " +
                "JOIN campground c ON c.campground_id = s.campground_id " +
                "JOIN park p ON p.park_id = c.park_id " +
                "WHERE p.park_id = ? AND r.reservation_id IS NULL";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);
        while(results.next()){
            Site site = mapRowToSite(results);
            sites.add(site);
        }
        return sites;
    }

    @Override
    public List<Site> getAvailableSitesForDates(int parkId, LocalDate startDate, LocalDate endDate){
        List<Site> sites = new ArrayList<>();
        String sql = "SELECT s.site_id, s.campground_id, s.site_number, s.max_occupancy, s.accessible, s.max_rv_length, s.utilities " +
                "FROM site s " +
                "LEFT JOIN reservation r ON r.site_id = s.site_id " +
                "JOIN campground c ON c.campground_id = s.campground_id " +
                "JOIN park p ON p.park_id = c.park_id " +
                "WHERE p.park_id = ? AND " +
                "(r.from_date BETWEEN ? AND ? IS NULL OR r.to_date BETWEEN ? AND ? IS NULL)";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId, startDate, endDate, startDate, endDate);
        while(results.next()){
            Site site = mapRowToSite(results);
            sites.add(site);
        }
        return sites;
    }

    private Site mapRowToSite(SqlRowSet results) {
        Site site = new Site();
        site.setSiteId(results.getInt("site_id"));
        site.setCampgroundId(results.getInt("campground_id"));
        site.setSiteNumber(results.getInt("site_number"));
        site.setMaxOccupancy(results.getInt("max_occupancy"));
        site.setAccessible(results.getBoolean("accessible"));
        site.setMaxRvLength(results.getInt("max_rv_length"));
        site.setUtilities(results.getBoolean("utilities"));
        return site;
    }
}
