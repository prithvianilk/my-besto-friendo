# Agent Rules

## Comments
- Prevent adding comments unless asked for. Don't add comments to explain the code, instead write meaningful method names and variable names that explain the intent.

## General coding standards

- Methods/functions with 3 or more parameters are a code smell. Use a DTO/record/object to group related parameters instead.

## Java writing standards

- Use java.lang.Objects's isNull and nonNull instead of null checks based on '=' operator

## Test Maintenance
- **Always update tests when modifying classes**: When you modify any class, method, or data structure, you MUST also update all related test files to reflect those changes.
- This includes:
  - Adding new fields to records/classes
  - Changing method signatures
  - Modifying data structures
  - Changing validation rules
  - Updating service logic
- Before completing any task, verify that all tests pass and are updated to match the current implementation.
- If a test file exists for a class you're modifying, update it in the same change set.

