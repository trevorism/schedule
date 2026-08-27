Feature: Scheduled task lifecycle
  A scheduled task can be created, retrieved, updated and deleted over the REST interface

  Background:
    Given the schedule application is alive

  Scenario: A scheduled task can be created, updated and deleted
    Given a dormant scheduled task exists
    Then the task can be retrieved by id
    And the task appears in the full list
    When the task type is updated to "weekly"
    Then the stored task type is "weekly"
    When the task is deleted
    Then the task can no longer be retrieved

  Scenario: An update cannot reassign the task to another tenant
    Given a dormant scheduled task exists
    When the task is updated with a tenantId of "bogus-tenant"
    Then the stored tenantId is not "bogus-tenant"
    And the stored tenantId is unchanged
