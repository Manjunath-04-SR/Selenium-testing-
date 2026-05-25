@smoke @login
Feature: User Login
  As a user of ProjectSphere
  I want to be able to log in with my credentials
  So that I can access the application based on my role

  Scenario: Login page displays all required fields
    Given the user is on the login page
    Then the login page should display email field, password field and Sign In button

  @admin
  Scenario: Admin logs in with valid credentials
    Given the user is on the login page
    When the admin enters valid credentials and clicks Sign In
    Then the admin dashboard should be displayed

  @pm
  Scenario: Project Manager logs in with valid credentials
    Given the user is on the login page
    When the PM enters valid credentials and clicks Sign In
    Then the PM dashboard should be displayed

  @negative
  Scenario: Login fails with invalid credentials
    Given the user is on the login page
    When the user enters email "invalid@test.com" and password "wrongpassword"
    Then a login error message should be displayed
