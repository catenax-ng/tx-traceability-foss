@TRACEFOSS-1282
Feature: ❓Test investigation flow


  @TRACEFOSS-1283 @INTEGRATION_TEST
  Scenario: Investigation flow
    Given I am logged into TRACE_X_A application
    And I create investigation
    When I send investigation
    And I am logged into TRACE_X_B application
    Then I can see notification was received
