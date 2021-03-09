# Loss Scenario attributes as tree
For each headcategory and subcategory the user can specify different attributes. These attributes are listed here.
## Failures related to the controller (for physical controllers)
The attributes are:
- Controller
- Description of the failure (textual description)

## Inadequate control algorithm
### Flawed implementation of the specified control algorithm
The attributes are:
- Controller
- Control Action
- Description of the failure of the algorithm(textual description)

###The specified control algorithm is flawed
The attributes are:
- Controller
- Control Action
- Description of the failure of the algorithm(textual description)

###The specified control algorithm becomes inadequate over time due to changes or degradation
The attributes are:
- Controller
- Control Action
- Description of the failure of the algorithm(textual description)
- Change / Degradation in the system (textual description)

###The Control algorithm gets changed by an attacker
The attributes are:
- Controller
- Control Action
- Description of the failure of the algorithm (textual description)
- Attacker (textual description)
- Proceeding the attacker (textual description)

## Unsafe control input
###UCA received from another controller (already addressed when considering UCAs from other controllers)
The attributes are:
- Control Action
- Source controller
- Target controller

## Inadequate process model
###Controller receives incorrect feedback/information
The attributes are:
- Controller
- Input
- Feedback
- Sensor
- Reason for not received information (dropdown)
- Reason description (textual description)

###Controller receives incorrect feedback/information
The attributes are:
- Controller
- Input
- Feedback
- Input box
- Sensor
- Misinterpreted hypothesis (textual description)
- Reason description (textual description)

###Controller does not receive feedback/information when needed (delayed or never received)
The attributes are:
- Controller
- Input
- Feedback
- Input box
- Sensor
- Misinterpreted hypothesis (textual description)
- Reason for not received information (dropdown)
- Reason description (textual description)