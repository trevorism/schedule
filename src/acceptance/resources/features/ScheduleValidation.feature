Feature: Scheduled task validation
  A scheduled task must have a name, a supported http method and an endpoint before it can be created

  Background:
    Given the schedule application is alive

  Scenario: A task without a name is rejected
    When a task is created without a name
    Then the request is rejected

  Scenario: A task without an endpoint is rejected
    When a task is created without an endpoint
    Then the request is rejected

  Scenario: A task without an http method defaults to post
    When a task is created without an http method
    Then the task is created successfully
    And the stored http method is "post"

  Scenario Outline: The unsupported http method <httpMethod> is rejected
    When a task is created with the http method "<httpMethod>"
    Then the request is rejected

    Examples:
      | httpMethod |
      | execute    |
      | head       |
      | options    |

  Scenario Outline: The http method <httpMethod> is stored lowercase
    When a task is created with the http method "<httpMethod>"
    Then the task is created successfully
    And the stored http method is "<stored>"

    Examples:
      | httpMethod | stored |
      | GET        | get    |
      | Post       | post   |
      | DELETE     | delete |

  Scenario: A task name is stored lowercase
    When a task is created with the name "MixedCase-Acceptance-Task"
    Then the task is created successfully
    And the stored name is "mixedcase-acceptance-task"

  Scenario: An endpoint without a scheme is prefixed with https
    When a task is created with the endpoint "schedule.action.trevorism.com/ping"
    Then the task is created successfully
    And the stored endpoint is "https://schedule.action.trevorism.com/ping"
