Feature: Enqueue decision
  Creating a task runs the same due-window check as the hourly sweep, but for that task alone.
  An immediate task is disabled the moment Cloud Tasks accepts it, so the create response reports
  whether the task was really enqueued without waiting for the callback to fire.

  Background:
    Given the schedule application is alive

  Scenario: An immediate task due now is enqueued and disabled
    Given an immediate task starting now
    Then the task was enqueued
    And the stored task is disabled

  Scenario: An immediate task due inside the next hour is enqueued
    Given an immediate task starting in 2 minutes
    Then the task was enqueued
    And the stored task is disabled

  Scenario: An immediate task due beyond the next hour is left for a later sweep
    Given an immediate task starting in 180 minutes
    Then the task was not enqueued
