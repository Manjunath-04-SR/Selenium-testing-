@smoke @admin @regression
Feature: Admin Dashboard
  As an Admin user
  I want to manage projects and teams
  So that I can oversee all project activity in ProjectSphere

  Background:
    Given the admin is logged in

  Scenario: Admin dashboard is displayed with KPI tiles
    Then the admin dashboard should show KPI tiles

  Scenario: Admin can navigate to Manage Projects
    When the admin navigates to Manage Projects
    Then the Manage Projects page should be displayed

  Scenario: Admin can navigate to Manage Teams
    When the admin navigates to Manage Teams
    Then the Manage Teams page should be displayed

  Scenario: Admin can open the Create Team dialog
    When the admin navigates to Manage Teams
    And the admin clicks New Team button
    Then the Create Team dialog should appear
    And the dialog should show team name field and project dropdown

  @regression
  Scenario: Admin can create a new team
    When the admin navigates to Manage Teams
    And the admin clicks New Team button
    And the admin creates a team with name "BDD Test Team"
    Then the Edit Team page should be displayed
