#!/bin/bash
cd /home/kavia/workspace/code-generation/weather-now-156046/weather_forecast_android_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

