# Automated Test Guide

## A. Overview

1. Unit test: Test of e.g. utility classes that independent of Android framework, located at `src/test` folder.
2. Instrumentation test: Test of e.g. Activity/Fragment classes that is within Android framework, located at `src/androidTest` folder.

## B. File structure

1. Directories are almost exactly like the one in source code.
2. The directory `sg.searchhouse.agentconnect.test` is for test specific classes.
3. For instrumental tests, there might be multiple instances of test of one class.

### Case 1: Single contexts example (e.g. `MainActivity`)

> sg.searchhouse.agentconnect.view.activity.main.**MainActivityTest**

### Case 2: Multiple contexts examples (for different authorization, intent extras into activity, etc.)

Context 1: Login as regular user
> sg.searchhouse.agentconnect.view.activity.main.main_activity/**MainActivity_LoginAsRegularUserTest**

Context 2: Login as premium user
> sg.searchhouse.agentconnect.view.activity.main.main_activity/**MainActivity_LoginAsPremiumUserTest**

## C. Method format

> fun **givenA_whenB_thenC**() {

A: Pre-requisite (optional)
B: Action
C: Expected result

### Example: Unit test

> fun **whenInputValidNumber_thenReturnTrue**() {

> fun **whenInputEmptyString_thenThrowError**() {

### Example: Instrumentation test

> fun **whenSwipeUp_thenDisplayDialog**() {

> fun **givenIsAuthorized_whenClickHomeReportButton_thenVisitHomeReportPage**() {