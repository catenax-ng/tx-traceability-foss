# Architecture documentation (arc42)

### Notice

This work is licensed under the [Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0).

* SPDX-License-Identifier: Apache-2.0
* Licence Path: <https://creativecommons.org/licenses/by/4.0/legalcode>
* Copyright (c) 2021,2022,2023 Contributors to the Eclipse Foundation
* Copyright (c) 2022, 2023 ZF Friedrichshafen AG
* Copyright (c) 2022 ISTOS GmbH
* Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
* Copyright (c) 2022,2023 BOSCH AG
* Source URL: <https://github.com/eclipse-tractusx/traceability-foss>

## Introduction and goals

This chapter gives you an overview about the goals of the service,
in which context the service runs and which stakeholders are involved.

## Requirements overview

### What is the purpose of the Trace-X application

* Empower all companies from SMEs to large OEMs to participate in parts traceability with an Open-Source solution to easily take part in Catena-X Traceability Use Case.
* It is a standalone application which can be self-hosted.
* Display the relations of the automotive value chain based on a standardized IT model.
* Overview and transparency across the supplier network enable faster intervention based on recorded events in the supply chain.
* Notifications/Messages regarding quality-related incidents and a tool for inspecting the supply chain.

### Essential features

* List and view manufactured parts based on BoM AsBuild
* List and view planned parts based on BoM AsPlanned
* Filter and search functionality on part views
* Show detailed information on manufactured parts from AAS description assets and Aspects
* Uses Submodels SerialPart, AssemblyPartRelationship and Batch
* List and view Supplier parts (AssemblyPartRelationship) based on BoM AsBuild lifecycle
* View parts and parts relations in a visualized parts tree
* Send and receive top-down notifications (quality investigations) along the supply chain
* Compliance with Catena-X Guidelines

## Quality goals

The following table entries define overall Trace-X quality goals. The order of topics does not imply a priority of the quality goals.

| Quality goal | Motivation and description |
| --- | --- |
| Running reference application for Catena-X Traceability | Consume traceability data, visualize it in a state-of-the-art frontend to the user and enable the exchange of notifications. |
| Accessible and easy to use | Enable SMEs to large enterprises. |
| Cloud agnostic solution | Trace-X is built as reference architecture and able to be run on different cloud solutions. It uses Helm, i.e. Helm charts, so that it can easily be deployed on different systems. |
| Trustworthy application | Use the Catena-X standards as a basis to fulfill the interoperability (with commercial as well as other solutions) and data sovereignty requirements. |
| Application reliability | The Trace-X architecture is set up so that provided part tree structures are consumed, aggregated and utilized to enable quality related actions such as notifications along the supply chain on which the costumers can rely. |
| Usability and user experience | Trace-X is aligned with the overarching UUX guidelines. This ensures ease of use for the user as well as a good user experience. |
| Security | Static Application Security Testing (SAST) and Dynamic Application Security Testing (DAST) are executed automatically and regularly. Findings are recorded and mitigated. |

## Stakeholder

The following table presents the stakeholders of Trace-X and their respective intentions.

| Who | Matters and concern |
| --- | --- |
| Software Developer | * Make an impact on Catena-X by participating in open-source software. * Example implementation for own use cases and applications. |
| Operating Environments | * Use Trace-X as a basis for industrialization. * Example implementation for own business applications, further features and fast ramp up. |
| Catena-X Partners | * Take part in Traceability Use Case. * See relationships in the value chain. * React on quality issues with standardized actions and notifications. |
| Larger OEMs / Tiers | * Want to use this implementation for further development and integration into their system landscape. |
| SMEs | * Need a solution to view data provided into the Catena-X network. * Act in a standardized way with partners within CX. |

## Architecture Constraints

### Technical Constraints

| Name | Description |
| --- | --- |
| Interoperability | Trace-X must use EDC together with Catena-X approved data models to guarantee interoperability between participants. |
| Data Sovereignty | Data owners and data consumers have to use usage policies for offering, consuming and therefore transferring data. |
| Kubernetes for Container Orchestration | Catena-X requires the application to run in a Kubernetes environment, deployed via Helm Charts. |
| Catena-X UUX Guidance (CX Style Guide) | Frontend follows Catena-X wide UUX consistency according to CX Style Guide |

### Organizational Constraints

| Name | Description |
| --- | --- |
| Schedule | Start of development in July 2022. Further development in alignment with the Foundation of the Catena-X Automotive Network e.V. requirements and timeline. |
| Process model | Iterative and incremental. SAFe Framework is used to align with Catena-X services, prerequisites, components and requirements to be Catena-X compatible. |
| Catena-X services / requirements | Trace-X needs to be Catena-X compliant and the application has to follow the CX standards as well as interact with the core services and components. |
| Release as Open Source | The source code, at least parts of it, are made available as open source and can be found in Github Catena-X ng as well as in Eclipse Tractus-X. |
| Technology Readiness Level (TRL) for Products developed within the CX Consortia | As Trace-X is a reference implementation, the Technology Readiness Level (TRL) must not be above TRL 8. |

### Political Constraints

| Name | Description |
| --- | --- |
| Open Source | FOSS licenses approved be the Eclipse foundation have to be used. |

### Development Conventions

| Name | Description |
| --- | --- |
| Architecture documentation | Architectural documentation of Trace-X reference application in arc42-Template terminology and structure. |
| Language | The project language is English to ensure the best possible accessibility for all participants. Therefore, classes, methods etc. are named in English and the documentation is written in English. |
| Code Coverage | More than 80% test coverage of the complete source code |

## System scope and context

Trace-X is an end user application to visualize and utilize data provided to the Catena-X network. This includes the traceability of manufactured parts and batches as well as the shipped and supplied components. To utilize the CX open ecosystem it is necessary to exchange information on serialized parts and batches with supply chain partners in a standardized, data sovereign and interoperable way. This section describes the environment of Trace-X, its intended users and which  systems and components it interacts with.

## Business context

Trace-X exchanges data with any other Traceability applications within the CX ecosystem. This is implemented by integrating the service into the CX network and the usage of required central components and services provided by CX.

![arc42_000](https://catenax-ng.github.io/tx-traceability-foss/docs/assets/arc42/arc42_000.png)

### User

Trace-X can be deployed, hosted and run by every party that is part of the Catena-X network. They need to be registered, have a BPN, technical user as well as provide valid credentials. An end-user in Trace-X can have roles provided by the CX Portal (User, Supervisor, Admin). As a prerequisite for Trace-X, valid data must already be provided to CX network since Trace-X only consumes data but has no import interface or data provisioning functionality.

### IRS

Item Relationship Service is the component that is used by Trace-X to fetch the data chains from Catena-X network. Data that is provided by IRS gets extracted, aggregated, transformed and is used for further actions in Trace-X. Further information can be found in the [IRS architecture documentation (arc42)](https://eclipse-tractusx.github.io/item-relationship-service/docs/arc42/).

### Catena-X network

Trace-X and IRS are retrieving necessary information and data from the Catena-X network (Users, digital twins, aspects, submodels, Business Partner information). If CX services are unavailable, Trace-X will not be able to perform most of its work.

### Any other Traceability App

Trace-X interacts with any other Traceability app using the CX standards. This enables sovereign data exchange as well as receiving and sending notifications to interact between different parties.

## Technical context

### Component overview

#### Trace-X API

We provide a REST API that is consumed by Trace-X frontend in order to deliver Trace-X related features such as quality-investigations or assets chain visibility.
Since Trace-X component is a very last component in the Catena-X ecosystem we are mostly depend on the other services and theirs APIs in other to deliver desired functionalities. The development of the services is not a part of the Trace-X application and each of the system that we utilize exposes REST API that we consume and interact directly.

Trace-X is a Spring Boot based application and is secured with the OpenID connector provider Keycloak and the OAuth2. This means for the company that utilize Trace-X component
it is required to obtain technical user in order to be authorized to get access to the external components within Catena-X ecosystem.

In order to use Trace-X frontend with Trace-X backend, users need to authenticate themselves in order to be authorized to get access to the Trace-X.
By the frontend UI users provide valid credentials and the system generates a bearer token that it gets from Keycloak and attaches it to the HTTP header parameter Authorization.
Then once a user is authorized and has proper role within Trace-X backend, the backend delegates HTTP calls to specific service in their behalf as technical user in order to fulfill specific functionality.

#### Registry API

![arc42_001](https://catenax-ng.github.io/tx-traceability-foss/docs/assets/arc42/arc42_001.png)

The Trace-X acts as a consumer of the Asset Administration Shell Registry component. The Trace-X contains a Restful client (REST template) that build a REST call to the mentioned Digital Twin Registry API based on its known URL (the AAS registry URL is configurable in the Trace-X).
Requests contain "assetIds" provided by the component during assets synchronization. Like described in the above section, the security aspect is required in order to achieve a REST call against the AAS Registry. As a response, the Trace-X gets the corresponding shells and shell descriptors utilized then later for assets synchronization. The HTTP(s) transport protocol is used for the REST call communication.

#### IRS API

![arc42_002](https://catenax-ng.github.io/tx-traceability-foss/docs/assets/arc42/arc42_002.png)

The Trace-X acts as a consumer of the IRS component. The Trace-X contains a Restful client (REST template) that build a REST call to the mentioned IRS API based on its known URL (the IRS URL is configurable in the Trace-X).
Request contains details required to start IRS fetch job provided by the component during assets synchronization. Like described in the above section, the security aspect is required in order to achieve a REST call against the IRS. As a response, the Trace-X gets the created job id and periodically pulls for the job details that contains assets that will be uploaded to the system. And as mentioned above, the transport protocol HTTP(S) is used for the REST call communication.

#### Portal API

![arc42_003](https://catenax-ng.github.io/tx-traceability-foss/docs/assets/arc42/arc42_003.png)

The Trace-X acts as a consumer of the Portal component. The Trace-X contains a Restful client (REST template) that build a REST call to the mentioned Portal API based on its known URL (the Portal URL is configurable in the Trace-X).
Request contains "bpns" provided by the component during sending notifications. Like described in the above section, the security aspect is required in order to achieve a REST call against the Portal. As a response, the Trace-X gets the corresponding BPN mappings to EDC urls where a notification should be send over. And as mentioned above, the transport protocol HTTP(S) is used for the REST call communication.

#### EDC API

![arc42_004](https://catenax-ng.github.io/tx-traceability-foss/docs/assets/arc42/arc42_004.png)

The Trace-X acts as a consumer of the EDC component. In Trace-X we communicate with EDC directly only for the sake of fulfilling quality-investigation functionality. Specific use cases can be viewed in [Runtime view](../runtime-view/index.adoc) section. For this purposes the integrated EDC clients in the Trace-X are responsible for creating restful requests to the EDC component. And as mentioned above, the transport protocol HTTP(S) is used for the REST call communication.

## Solution strategy

This section contains summarized architectural overview. A comparison of the most important goals and the corresponding solution approaches.

## Introduction

Following table describes the quality goals of Trace-X (see chapter quality goals) and their matching solution approaches.

| Quality goal | Matching approaches in the solution |
| --- | --- |
| Running reference application for Catena-X Traceability | * Published open source, Trace-X application can be used as a reference by anyone. |
| Accessible and easy to use | * Established programming languages are used. * Backend written in Java * Frontend written in Typescript based on the Angular framework. |
| Cloud agnostic solution | * Helm charts to support the deployment of the application in a Kubernetes environment |
| Application reliability | * Data source is the Catena-X notwork. Data is fetched with IRS directly from the data owner and the Digital Twin Registry of CX. * Trace-X can be hosted decentralized since it is an open source reference implementation. |
| Security | * Static Application Security Testing (SAST) and Dynamic Application Security Testing (DAST) are executed automatically and regularly with tools as part of the pipeline. |

## Technology

Trace-X is developed using Java and the Spring Boot framework for the Backend and Typescript based on the Angular framework for the Frontend. This decision was taken due to the support of the frameworks as well as technical knowledge of the team members.

The application can be hosted using Docker and Kubernetes. This is commonly used and widespread. With this the application has no vendor lock in regarding the hosting provider.

The communication between Frontend and Backend is done using REST APIs. This is the standard method in the Catena-X landscape and makes the application components easy to use for any third party client.

As the database to store parts information etc. PostgreSQL Database is used.

## Structure

Trace-X is divided into two components: Frontend and Backend.
It roughly can be broken down into the following parts:

* Asset controllers to get the asset information
* Dashboard controller to get dashboard related summed up information
* Registry controller to fetch assets from the Digital Twin Registry
* Notification controllers to get notification information
* Submodel controller for providing asset data functionality

The backend does a request to the Digital Twin Registry utilizing the Registry controller. Extracted data from the response is made available through the Asset controller and the Dashboard controller to the Frontend.

## Building block view

## Whitebox overall system

### Component diagram

![arc42_005](https://catenax-ng.github.io/tx-traceability-foss/docs/assets/arc42/arc42_005.png)
```bash
The interfaces show how the components interact with each other and which interfaces the TraceX is providing.

Component Diagram

```

### Component description

|Components |Description
|IRS
|The IRS consumes relationship information across the CX-Network and builds the graph view. Within this Documentation, the focus lies on the IRS

|EDC Consumer
|The EDC Consumer Component is there to fulfill the GAIA-X and IDSA-data sovereignty principles. The EDC Consumer consists out of a control plane and a data plane.

|EDC Provider
|The EDC Provider Component connects with EDC Consumer component and  forms the end point for the actual exchange of data. It handles automatic contract negotiation and the subsequent exchange of data assets for connected applications.

|Submodel Server
|The Submodel Server offers endpoints for requesting the Submodel aspects.

|IAM/DAPS
|DAPS as central Identity Provider

## Level 1

### Component diagram

![arc42_006](https://catenax-ng.github.io/tx-traceability-foss/docs/assets/arc42/arc42_006.png)

### Component description

| Components | Description |
| --- | --- |
| **Trace-X** | Trace-X is a system allowing the user to review the parts/assets catalogue, start a quality investigations and receive quality alerts |
| **Trace-X API** | The **Trace-X API** is the Interface over which the Data Consumer is communicating. |
| **AssetsController** | The **AssetsController** provides a REST Interface for retrieving the parts/assets information. |
| **DashboardController** | The **DashboardController** provides a REST Interface for retrieving overall statistics displayed on a dashboard screen. |
| **RegistryController** | The **RegistryController** provides a REST Interface for retrieving the data from parts registry. |
| **AssetRepository** | The **AssetRepository** is a component responsible for storing and getting assets from database. |
| **BPNRepository** | The **BPNRepository** is a component which stores BPN -> company name mappings. |
| **NotificationsRepository** | The **NotificationsRepository** is a component responsible for storing and holding status of sent/received notifications |
| **Database** | Place for storing assets, relations as well as sent/received notifications |

## Runtime view

This section describes the different functionalities of TraceX-FOSS application

## Assets

## Scenario 1: Return Assets

This section describes what happens when user lists stored assets.
In this example, the user requests as built assets.
The same can be done with as planned assets.

![arc42_007](https://catenax-ng.github.io/tx-traceability-foss/docs/assets/arc42/arc42_007.png)

##### Overview

When a user requests stored assets, TraceX-FOSS checks if the user has an adequate role ('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER').
If yes, then the endpoint returns a pageable result of assets.

The returned pageable result can be empty if no suitable asset has been found.

## Scenario 2: Return specific Assets

This section describes what happens when user searches for a specific asset.
This example shows the request of one as built asset.
The same can be done with as planned assets.

![arc42_008](https://catenax-ng.github.io/tx-traceability-foss/docs/assets/arc42/arc42_008.png)

##### Overview

When a user requests a specific asset, TraceX-FOSS checks if the user has an adequate role ('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER'). If yes, then the endpoint returns a precise Asset for the given assetId, if it is found.

If no asset has been found for the given ID, an AssetNotFoundException is thrown.

## Notifications

## Receive Quality Investigation

This sequence diagram describes the process of receiving a quality investigation from another Traceability partner.

![arc42_009](https://catenax-ng.github.io/tx-traceability-foss/docs/assets/arc42/arc42_009.png)

### Overview

As for the sending of a quality investigation also for receiving of a notification EDC is used to push data from a sender to a receiver.
To enable receiving a notification by a partner you need to

* Create notification endpoint for qualitynotifications/receive
* Create EDC assets
* Create EDC usage policies
* Create EDC contract definitions

Trace-X implements a functionality to create the assets and their corresponding policies in the admin panel.

With the notification asset is possible to enable EDC contract negotiation and EDC data transfer based on access policies defined. Only if the sender is able to browse the asset in the catalog offer and perform a successful contract negotiation there will be the possibility to push a notification to the specified http endpoint on the receiver side.

## Send Quality Investigation

This sequence diagram describes the process of sending a quality investigation between Traceability applications.

![arc42_010](https://catenax-ng.github.io/tx-traceability-foss/docs/assets/arc42/arc42_010.png)

### Overview

For the notification feature EDC is used to push data from a sender to a receiver.
To enable sending respective more precisely receiving a notification by a partner you need to

* Create notification endpoint for qualitynotifications/receive
* Create EDC assets
* Create EDC usage policies
* Create EDC contract definitions

Trace-X implements a functionality to create the assets and their corresponding policies in the admin panel. With the notification asset is possible to enable EDC contract negotiation and EDC data transfer process so that the quality investigation can be pushed by the sender.

In the above UML sequence diagram the sending of quality notifications from Trace-X to a receiver (any other Traceability application) is described.

## Data consumption

This sequence diagram describes the process of fetching data from a DTR and the Catena-X ecosystem.

![arc42_011](https://catenax-ng.github.io/tx-traceability-foss/docs/assets/arc42/arc42_011.png)

### Overview

Data is fetched by a Trace-X instance using Digital Twin Registry (DTR), Item Relationship Service (IRS) and Trace-X Consumer EDC. Data has to be provided to the Catena-X network using a EDC Provider, since Trace-X is not a data provider and therefore no possibility for data provisioning with Trace-X exists.
For digital twins the Asset Administration Shell (AAS) standard is used. For fetching data with Trace-X, a Digital Twin Registry and an IRS instance are required. Data should represent parts, supplier and customer parts, parts tree / parts relations.

## Data Provisioning

This sequence diagrams describes the process of importing data from a Trace-X Dataformat

Modul 1

Data will be imported by the Trace-X Frontend into Trace-X backend and will be persisted as asset by a Trace-X instance in a transient state.
The raw data which is needed for the shared services (DTR / EDC) will be persisted as well.

![arc42_012](https://catenax-ng.github.io/tx-traceability-foss/docs/assets/arc42/arc42_012.png)
```bash

```

Modul 2

The frontend is able to select assets and publish / syncronize them with the shared services. DTR / EDC / Submodel API.

![arc42_013](https://catenax-ng.github.io/tx-traceability-foss/docs/assets/arc42/arc42_013.png)
```bash

```

Modul 3

The backend is able to persist the data in the DTR / EDC and allows to use IRS for resolving assets.

![arc42_014](https://catenax-ng.github.io/tx-traceability-foss/docs/assets/arc42/arc42_014.png)
```bash

```

### Overview

Data has to be provided to the Catena-X network using a EDC Provider, since Trace-X is not a data provider and therefore no possibility for data provisioning with Trace-X exists.
For digital twins the Asset Administration Shell (AAS) standard is used. For fetching data with Trace-X, a Digital Twin Registry and an IRS instance are required. Data should represent parts, supplier and customer parts, parts tree / parts relations.

## Deployment view

## Cross-cutting concepts

## Entity Relationship Model

Please be informed that the 'as-planned' version currently lacks the database relations. However, kindly maintain the Entity-Relationship Model (ERM) in its current state.

```bash
image::./assets/arc42/arc42_015.png[]
