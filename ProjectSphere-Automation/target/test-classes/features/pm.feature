@smoke @pm @regression
Feature: Project Manager Dashboard
  As a Project Manager
  I want to manage my projects and teams
  So that I can track and update project activity in ProjectSphere

  Background:
    Given the PM is logged in

  Scenario: PM dashboard is displayed with quick action cards
    Then the PM dashboard should show quick action cards

  Scenario: PM can navigate to My Projects
    When the PM navigates to My Projects
    Then the My Projects page should be displayed

  Scenario: PM can navigate to My Teams
    When the PM navigates to My Teams
    Then the My Teams page should be displayed

  Scenario: PM can open the Create Project dialog
    When the PM clicks New Project button
    Then the Create Project dialog should appear

  @regression
  Scenario: PM can create a new project and it appears in My Projects
    When the PM creates a project with name "BDD Cucumber Project"
    Then the project "BDD Cucumber Project" should appear in My Projects list
