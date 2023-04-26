# schedule 
![Build](https://github.com/trevorism/schedule/actions/workflows/deploy.yml/badge.svg)
![GitHub last commit](https://img.shields.io/github/last-commit/trevorism/schedule)
![GitHub language count](https://img.shields.io/github/languages/count/trevorism/schedule)
![GitHub top language](https://img.shields.io/github/languages/top/trevorism/schedule)

Schedule is a webapp that enables scheduled tasks.

Current version: 1.1.0

Deployed to [Schedule](https://schedule.action.trevorism.com/)

Uses a rather ingenious way of scheduling which optimizes for cost on GCP. 
The app keeps track of schedules as data, and enqueues them as Cloud Tasks by checking hourly if the tasks should be invoked within the next hour.

