## 2.2.0

Add back multitenancy support. Update dependencies.

## 2.1.0

Removed multitenancy support as it was bugged.

## 2.0.0

Make schedules id based instead of name based. Add acceptance tests.

## 1.4.0

Update micronaut and other dependencies.

## 1.3.0

Enable multi-tenancy by allowing enqueued schedules 
to request an internal token in order to authenticate with other services.

## 1.2.0

Fix issue with date serialization.

## 1.1.0

Fix issue with date serialization.

## 1.0.0

Upgrade to java 17, micronaut.

## 0.9.1

Migrate to schedule.action.trevorism.com
 
## 0.9.0

Update dependencies and deploy to github actions.

## 0.8.0

Added another cadence, weekly.

Fixed several bugs related to oddities with scheduling over a day or year break.

## 0.7.0

Provides scheduled tasks for free ;)

Allowable cadences:  Immediate (one time), Daily, Hourly

Tasks can be scheduled to start at any time (now or the future)
