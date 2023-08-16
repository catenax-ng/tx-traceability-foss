# Users Manual

### General information

Accessing the application is done by the URL provided by the hosting company.
You can have three roles for the users:

* User
* Supervisor
* Admin

To login use the credentials provided by the hosting company.

### Navigation

Navigation is done based on the top menu.

![navigation-overview-r3-1](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/navigation-overview-r3-1.png)

#### Dashboard

Provides a fast overview about the amount of manufactured and supplied / customer (other) parts and batches, as well as the amount of open investigations and alerts.
Lists the five newest quality investigations and alerts to get an overview of the current state.

#### Parts

Navigates to the own manufactured parts and batches list view.

#### Other parts

Navigates to the supplier and customer parts and batches list view.

#### Quality investigation

Navigates to the inbox and outgoing investigations.

#### Quality alert

Navigates to the inbox and outgoing alerts.

#### Administration

Only applicable for the admin user role. Possibility to check the network status based on logfiles and will provide access to configuration possibilities for the application.

#### Logout

Use the Icon in upper right corner to open User details and sign out button.

#### Language

![language-icon](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/language-icon.png) Change language.\
Supported languages:

* English
* German

### Parts

List view of the own manufactured parts and batches.
Gives detailed information on the assets registered in the Digital Twin Registry of Catena-X for the company. This includes data based on the aspect models of Use Case Traceability: SerialPart, Batch.
Parts that are in a quality alert are highlighted yellow.

![parts-list-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/parts-list-view.png)

#### Parts select / Quality alert

Select one or multiple child components/parts/batches that are build into your part. Selection will enable you to create a quality alert (notification) to your customers. The quality alert will be added to a queue (queued & requested inbox) and not directly sent to the customers.

Once the quality alert is created you will get a pop-up and can directly navigate to the inbox for further action.

#### Part details

Clicking on an item in the list opens "Part details" view.
More detailed information on the asset is listed as well as a part tree that visually shows the parts relations.

![parts-list-detailed-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/parts-list-detailed-view.png)

##### Overview

General production information. Information on the quality status of the part/batch.

##### Relations

Part tree based on SingleLevelBomAsBuilt aspect model. Dependent on the semantic data model of the part the borders are in a different color. A green border indicates that the part is a SerialPart. A yellow border indicates that the part is a piece of a batch.

It is possible to adjust the view of the relationships by dragging the mouse to the desired view. Zooming in/out can be done with the corresponding control buttons.

![open-new-tab](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/open-new-tab.png) Open part tree in new tab to zoom, scroll and focus in a larger view. A minimap on the bottom right provides an overview of the current position on the part tree.

##### Manufacturer data

Detailed information on the IDs for the manufactured part/batch.

##### Customer data

Information about the identifiers at the customer for the respective part/batch.

### Other parts

List view of the supplied/delivered parts and batches (Supplier parts / Customer parts).
Gives detailed information on the assets registered in the Digital Twin Registry of Catena-X. This includes data based on the aspect models of Use Case Traceability: SerialPart, Batch.

#### Supplier parts

List view of supplied parts and batches.
Supplier parts that are in a quality investigation are highlighted yellow.

##### Supplier parts select / Quality Investigation

Select one or multiple supplier parts. Selection will enable you to create a quality investigation (notification) to your supplier. The quality investigation will be added to a queue (queued & requested inbox) and not directly be sent to the supplier.

Once the quality investigation is created you will get a pop-up and can directly navigate to the inbox for further action.

#### Supplier part details

Clicking on an item in the list opens "Part details" view.
More detailed information on the asset is listed.

![supplier-parts-list-detailed-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/supplier-parts-list-detailed-view.png)

##### Overview

General production information. Information on the quality status of the supplier part/batch.

##### Manufacturer data

Detailed information on the IDs for the supplier part/batch.

##### Customer data

Information about the identifiers at the customer (in this case own company) for the respective part/batch.

#### Customer Parts

List view of customer parts and batches.
Customer Parts that are in a quality alert are highlighted yellow.

#### Customer part details

Clicking on an item in the list opens "Part details" view.
More detailed information on the asset is listed.

![customer-parts-list-detailed-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/customer-parts-list-detailed-view.png)

##### Overview

General production information. Information on the quality status of the supplier part/batch.

##### Manufacturer data

Detailed information on the IDs for the supplier part/batch.

##### Customer data

Information about the identifiers at the customer for the respective part/batch.

### Quality investigation

Inbox for received quality investigations and "Queued & Requested" inbox for outgoing draft as well as already sent investigations.

![investigations-list-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/investigations-list-view.png)

![notification-drafts](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/notification-drafts.png) Received investigations.

Investigations received by a customer. Those notifications specify a defect or request to investigate on a specific part / batch on your side and give feedback to the customer.

![notification-send](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/notification-send.png) Queued & Requested investigations.

Notifications in the context of quality investigations that are in queued/draft status or already requested/sent to the supplier. Those notifications specify a defect or request to investigate on a specific part / batch on your suppliers side and give feedback back to you.

* Queued status: Quality investigation is created but not yet released.
* Requested status: Quality investigation is sent to the supplier.

#### Quality investigation context action

Select the three dots icon on the right side of an investigation entry to open the context menu. From there it is possible to open the investigation detailed view or change the status of an investigation. Only the possible status transition will show up.

![notification-context-action](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/notification-context-action.png)

Changing the status of an investigation will open a modal in which the details to the status change can be provided and completed.

![investigation-context-action-modal](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/investigation-context-action-modal.png)

A pop-up will notify you if the status transition was successful.

#### Quality investigation Detail view

The investigation detail view can be opened by selecting the corresponding option in the context menu.

![investigation-detail-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/investigation-detail-view.png)

##### Overview

General information about the notification.

##### Affected Parts

Listed parts that are assigned to the selected alert.

##### Supplier parts

Detailed information for child parts assigned to a notification

##### Message History

Displays all state transitions including the reason/description of the transition that were done on the notification to get an overview of the correspondence between sender and receiver.

##### Quality investigation action

All possible state transitions are displayed in form of buttons (upper right corner). There the desired action can be selected to open a modal in which the details to the status change can be provided and completed.

#### Quality investigation status

Following status for a quality investigation (notification) are possible:

| Status | Description |
| --- | --- |
| Queued | A quality investigation that was created by a user but not yet sent to the receiver. |
| Requested | Created quality investigation that is already sent to the receiver. |
| Cancelled | Created quality investigation that is not yet sent to the receiver and got cancelled on sender side before doing so. It is no longer valid / necessary. |
| Received | Received notification from a sender which needs to be investigated. |
| Acknowledged | The receiver acknowledged to work on the received inquiry. |
| Accepted | The receiver accepted the inquiry. Issue on part/batch detected. |
| Declined | The receiver declined the inquiry. No issue on part/batch detected. |
| Closed | The sender closed the quality investigation and no further handling with it is possible. |

#### Quality investigation status flow

Notifications always have a status. The transition from one status to a subsequent status is described in the below state model.

The Sender can change the status to closed from any status. The receiver can never change the status to closed.

The legend in the below state diagram describes who can set the status. One exception to this rule: the transition from status SENT to status RECEIVED is done automatically once the sender receives the Http status code 201.

![Notification state model](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/notificationstatemodel.png)

### Quality alert

Inbox for received quality alerts and "Queued & Requested" inbox for outgoing draft as well as already sent alerts.

![alerts-list-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/alerts-list-view.png)

![notification-drafts](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/notification-drafts.png) Received alerts.

Alerts received by a supplier. Those notifications specify a defect or request to informed on a specific part / batch on your side to be informed and give feedback to the supplier.

![notification-send](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/notification-send.png) Queued & Requested alerts.

Notifications in the context of quality alerts that are in queued/draft status or already requested/sent to the customer. Those notifications specify a defect or request to inform on a specific part / batch on the customer side and give feedback back to you.

* Queued status: Quality alert is created but not yet released.
* Requested status: Quality alert is sent to the customer.

#### Quality alert context action

Select the three dots icon on the right side of an alert entry to open the context menu. From there it is possible to open the alert detailed view or change the status of an alert. Only the possible status transitions will show up.

![notification-context-action](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/notification-context-action.png)

Changing the status of an alert will open a modal in which the details to the status change can be provided and completed.

![alert-context-action-modal](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/alert-context-action-modal.png)

A pop-up will notify you if the status transition was successful.

#### Quality alert Detail view

The alert detail view can be opened by selecting the corresponding option in the context menu.

![alert-detail-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/alert-detail-view.png)

##### Overview

General information about the notification.

##### Affected parts

Listed parts that are assigned to the selected alert.

##### Supplier parts

Detailed information for child parts assigned to a notification

##### Message History

Displays all state transitions including the reason/description of the transition that were done on the notification to get an overview of the correspondence between sender and receiver.

##### Quality investigation action

All possible state transitions are displayed in form of buttons (upper right corner). There the desired action can be selected to open a modal in which the details to the status change can be provided and completed.

#### Quality alert status

Following status for a quality alert (notification) are possible:

| Status | Description |
| --- | --- |
| Queued | A quality alert that was created by a user but not yet sent to the receiver. |
| Requested | Created quality alert that is already sent to the receiver. |
| Cancelled | Created quality alert that is not yet sent to the receiver and got cancelled on sender side before doing so. It is no longer valid / necessary. |
| Received | Received notification from a sender which needs to be aware of. |
| Acknowledged | The receiver acknowledged to work on the received inquiry. |
| Accepted | The receiver accepted the inquiry. Issue on part/batch is known. |
| Declined | The receiver declined the inquiry. No issue on part/batch is known. |
| Closed | The sender closed the quality alert and no further handling with it is possible. |

#### Quality alert status flow

Notifications always have a status. The transition from one status to a subsequent status is described in the below state model.

The Sender can change the status to closed from any status. The receiver can never change the status to closed.

The legend in the below state diagram describes who can set the status. One exception to this rule: the transition from status SENT to status RECEIVED is done automatically once the sender receives the Http status code 201.

![Notification state model](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-guide/notificationstatemodel.png)
