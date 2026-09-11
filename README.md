# Case Import Server

[![Actions Status](https://github.com/gridsuite/case-import-server/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/gridsuite/case-import-server/actions)
[![Coverage Status](https://sonarcloud.io/api/project_badges/measure?project=org.gridsuite%3Acase-import-server&metric=coverage)](https://sonarcloud.io/component_measures?id=org.gridsuite%3Acase-import-server&metric=coverage)
[![MPL-2.0 License](https://img.shields.io/badge/license-MPL_2.0-blue.svg)](https://www.mozilla.org/en-US/MPL/2.0/)
[![Slack](https://img.shields.io/badge/slack-powsybl-blueviolet.svg?logo=slack)](https://join.slack.com/t/powsybl/shared_invite/zt-36jvd725u-cnquPgZb6kpjH8SKh~FWHQ)

## Description

The **case-import-server** is a microservice of the [GridSuite](https://github.com/gridsuite) platform providing a single REST endpoint to **import a power network case file and automatically store it in a target directory** of the GridSuite tree, based on the declared origin of the file.

It provides the following capabilities:

- **Import a case file**: upload a case file via multipart POST; the file itself is **not stored by this service** but is forwarded as-is to `case-server`, which performs the actual storage and returns a case UUID.
- **Route by origin**: the caller specifies a `caseFileSource` (origin) parameter; the service maps it to a configured target directory name via `case-import-server.target-directories`. If the origin is unknown, the import is rejected.
- **Create the directory element**: once the case is stored, a corresponding element is created in the target directory by calling `directory-server`, with the case name defaulting to the uploaded file's base name if not explicitly provided.

---

## Technical Stack

- Spring Boot (Web, Actuator)
- Spring Cloud Stream (RabbitMQ, inherited from `powsybl-ws-commons`)
- PowSyBl Commons (case file name/format utilities)
- API documentation: OpenAPI / Swagger (`springdoc`)
- Micrometer / Prometheus

---

## Configuration

Target directories are mapped per case origin under `case-import-server.target-directories`:

```yaml
case-import-server:
  target-directories:
    origin1: case_import_directory_1
    default: Automatic_cases_import
```

A `default` entry is always guaranteed at startup (defaulting to `Automatic_cases_import` if not overridden).

---

## Interactions with Other Microservices

```text
┌──────────────────────────┐
│    case-import-server    │
└──────────────────────────┘
        │                │
        ▼                ▼
  case-server      directory-server
 (delegates raw   (create the element
  file storage)    in the target directory)
```

- **case-server**: `case-import-server` does not store the file itself; it delegates raw storage by forwarding the upload (as multipart) to `POST /v1/cases`, which returns the case UUID.
- **directory-server**: once the case is stored, an element (`CASE` type) is created in the resolved target directory via `POST /v1/directories/paths/elements`.

---

## Why This Service Exists

Both `case-server` and `case-import-server` expose a `POST /v1/cases` multipart upload endpoint, but they are consumed differently:

- **`case-server`**'s upload endpoint is the actual main storage entry point, used directly by other micro-services that need low-level control over case storage

- **`case-import-server`**'s upload endpoint is not called today by any other backend microservice. It is reachable through the **gateway**'s generic reverse proxy, meaning its consumers are **external clients** that:
  - don't know about GridSuite's directory model or UUIDs,
  - want to drop a file under a named "origin" and have it filed automatically into the right GridSuite directory,
  - shouldn't need to orchestrate two separate calls (`case-server` + `directory-server`) themselves.


---

