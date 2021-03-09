#Documentation to the concept dashboard
##General
Due to the current structure of the project (XSTAMPP 4.0) it is not possible to implement a detailed and comprehensive dashboard with our current schedule.
This is the case, because in the database at the time to the respective entities exclusively the information 'last edited' is stored. This is the only information that could be accessed for analysis and statistics. 
For our ideas however we needed several pieces of information each time making new changes to each and every entity in the frontend, backend, and database. Here one could distinguish between project-specific and project-unspecific elements.

## Already available
In the frontend the implementation of the feature exists in the class dashboard and in the backend there is no function for the feature, because it should only represent a dummy.
The dashboard can only be reached with the link ++'project-overview /:dashboard'++ and must be fully integrated in the project as soon as it is used.
The dashboard is constructed by having the project-selection at the top and below this a three-column layout with cards.
The project-selection can not be edited individually by the user. The view always stays exactly as it is because the dashboard should be displayed immediately after login and replace the current project-selection. Therefore, it is important that the user can not delete the project-selection from the dashboard. You can wonder if he has the possibility to move the project-selection or change the size. This function is not yet implemented. The project-selection is fully functional with all functions that the project-selection has after login.
The cards under the project-selection can be edited with the icon at the top right. This editing allows deleting and moving the cards.
The maps currently consist of only two strings and an image which are for illustrative purposes only and have no connection to the backend.
The cards can currently be moved, but the content of the cards changes, depending on which of the three columns the card is placed. This is a point that still needs to be worked on, that if the cards are moved and the contents of the cards remain unchanged.

## Some content ideas for the dashboard
- ** Entitie of the day: ** Random Entitie is selected from the project and displayed.
- ** Project leader: ** If you are the leader in a project, you have the option of displaying a special view for this project in the dashboard. This could, for example, have a counter for how long the project is still running.
++ Problem: ++ This function does not exist in the project, that someone can add spectific information for a project.
- ** Recent changes: ** Each user has the opportunity to see the entities he has previously edited or created and which have been changed by other colleagues in a set period of time.
 ++ Problem: ++ As described in general, currently only 'last edited' is stored for each entity. Thus one would have to implement the required information with each entity even extra.
- ** Quicktable: ** Here you can select for each entity for example Losses, Hazards or Loss Scenarios a kind of preview of the current table of a project. This table can create from each entity in the project.
- ** Number of elements: ** Here you can display different types of diagrams. The content is the number of already created entites. For example, there are 20 Losses, 50 Hazards and 300 UCA. These values are taken out of the database.
- ** Statistics with the function Mark-Incomplete-Entities: ** With this function all possible statistics can be created. These can be very extensive, but also clear. Here you have all options open as soon as the function is finished implemented (currently this is not yet the case).
- ** Current number of users: ** Here are the current users who have the project currently displayed. Maybe as number or name.
++ Problem: ++ Currently we still have no privacy policy from Zendas and they also need a long time until what happens. Adding an new feature of working directly with personal data makes it even harder and more complicated to obtain a privacy policy.
- ** New User: ** New users who have registered in a certain period. Same problem as with 'Current user numbers'.
- ** Users online: ** All Nutezr that are currently online are displayed here. This can be done, for example, with an overpurple from the locks, for example by asking which user has queried a lock in the last 30 minutes.
