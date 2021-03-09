Insert Into project (id, name, description, created_at) values ('123e4567-e89b-12d3-a456-426655440000','blub','a short description', '2016-06-22 19:10:25-07');
Insert Into project (id, name, description, created_at) values ('00112233-4455-6677-8899-aabbccddeeff','Test','a longeeeeeeer description', '2016-06-22 19:10:25');

Insert Into system_description (id, description, last_edited, last_editor_displayname) Values ('123e4567-e89b-12d3-a456-426655440000','blub blub blub
    blub blub blubblub blub blubblub blub blubblub blub blub
    blub blub blubblub blub blubblub blub blubblub blub blub
    blub blub blubblub blub blubblub blub blubblub blub blub',
    '2016-06-01 00:00','bluberer');
																						  
Insert Into system_description (id, description, last_edited, last_editor_displayname) Values ('00112233-4455-6677-8899-aabbccddeeff','TestTestTestTest
    TestTestTestTestTestTestTestTestTestTestTestTestTestTest
    TestTestTestTestTestTestTestTestTestTestTestTestTestTest
    TestTestTestTestTestTestTestTestTestTestTestTestTestTest',
    '2016-07-01 01:00','Testerer');
																						  
Insert Into loss (id, project_id, name, description, last_edited, last_editor_displayname) Values (
    0,'123e4567-e89b-12d3-a456-426655440000','Verluste von Blubs','Es werden Blubs verloren','2017-07-01 01:00','bluberer');
Insert Into loss (id, project_id, name, description, last_edited, last_editor_displayname) Values (
    0,'00112233-4455-6677-8899-aabbccddeeff','Verluste von Tests','Es werden Tests verloren','2017-07-01 01:00','Testerer');
	
Insert Into hazard (id, project_id, name, description, last_edited, last_editor_displayname) Values(
    0,'123e4567-e89b-12d3-a456-426655440000','Blub Transport hat einen Unfall', 'Blub Transport f�hrt �ber die Bluberstra�e und crashed in einen zweiten Blub Transport',
    '2017-07-01 01:00','bluberer');
Insert Into hazard (id, project_id, name, description, last_edited, last_editor_displayname) Values(
    0,'00112233-4455-6677-8899-aabbccddeeff','Test Transport hat einen Unfall', 'Test Transport f�hrt �ber die Teststra�e und crashed in einen zweiten Test Transport',
    '2017-07-01 01:00','Testerer');
	
Insert Into hazard_loss_link (hazard_id,project_id,loss_id) Values(0,'123e4567-e89b-12d3-a456-426655440000',0);
Insert Into hazard_loss_link (hazard_id,project_id,loss_id) Values(0,'00112233-4455-6677-8899-aabbccddeeff',0);

Insert Into sub_hazard (id, project_id, name, description, parent_id, last_edited, last_editor_displayname) 
    Values(0, '123e4567-e89b-12d3-a456-426655440000', 'Blub Transport hat einen Unfall durch Beschleunigung', 'Blub Transport f�hrt �ber die Bluberstra�e, beschleunigt und crasht in einen zweiten Blub Transport',
        0,'2017-07-01 01:00','bluberer');
Insert Into sub_hazard (id, project_id, name, description, parent_id, last_edited, last_editor_displayname) 
    Values(0, '00112233-4455-6677-8899-aabbccddeeff', 'Test Transport hat einen Unfall durch Beschleunigung', 'Test Transport f�hrt �ber die Testerstra�e, beschleunigt und crasht in einen zweiten Test Transport',
        0,'2017-07-01 01:00','Testerer');
		  
Insert Into system_constraint (id, project_id, name, description, last_edited, last_editor_displayname) Values(
    0,'123e4567-e89b-12d3-a456-426655440000', 'Blub constraint', 'Blub constraint beschreibung',	'2017-07-01 01:00', 'bluberer');
Insert Into system_constraint (id, project_id, name, description, last_edited, last_editor_displayname) Values(
    0,'00112233-4455-6677-8899-aabbccddeeff', 'Test constraint', 'Test constraint beschreibung',	'2017-07-01 01:00', 'Testerer');
	
Insert Into system_constraint_hazard_link (hazard_id,system_constraint_id,project_id) Values(
    0,0,'123e4567-e89b-12d3-a456-426655440000');
Insert Into system_constraint_hazard_link (hazard_id,system_constraint_id,project_id) Values(
    0,0,'00112233-4455-6677-8899-aabbccddeeff');

Insert Into sub_system_constraint (id, project_id, sub_hazard_project_id,hazard_id, parent_id, sub_hazard_id, name, description, last_edited, last_editor_displayname)
    Values(0,'123e4567-e89b-12d3-a456-426655440000','123e4567-e89b-12d3-a456-426655440000',0, 0, 0, 'Blub sub constraint', 'Blub sub constraint beschreibung', '2017-07-01 01:00', 'bluberer');
Insert Into sub_system_constraint (id, project_id,sub_hazard_project_id,hazard_id, parent_id, sub_hazard_id, name, description, last_edited, last_editor_displayname)
    Values(0,'00112233-4455-6677-8899-aabbccddeeff','123e4567-e89b-12d3-a456-426655440000',0, 0, 0, 'Test sub constraint', 'Test sub constraint beschreibung', '2017-07-01 01:00', 'Testerer');
	
Insert Into box (id, name, project_id, box_type, x, y, height, width)
    Values (0,'bluberbox','123e4567-e89b-12d3-a456-426655440000','Controller',1,2,3,4);
Insert Into box (id, name, project_id, box_type, x, y, height, width)
    Values (0,'Testerbox','00112233-4455-6677-8899-aabbccddeeff','Controller',1,2,3,4);
	
Insert Into arrow (id, project_id, source, destination, label, arrow_type)
    Values (0,'123e4567-e89b-12d3-a456-426655440000',0,0,'bluberpfeil','Feedback');
Insert Into arrow (id, project_id, source, destination, label, arrow_type)
    Values (0,'00112233-4455-6677-8899-aabbccddeeff',0,0,'Testerpfeil','Feedback');
	
Insert Into controller (id,project_id,name,description)
    Values (0,'123e4567-e89b-12d3-a456-426655440000','blubcontroller','Das ist der Blubcontroller');
Insert Into controller (id,project_id,name,description)
    Values (0,'00112233-4455-6677-8899-aabbccddeeff','testcontroller','Das ist der Testcontroller');
	
Insert Into controlled_process (id, project_id, name, description)
    Values (0,'123e4567-e89b-12d3-a456-426655440000','blub controlled process','Das ist der Blub controlled process');
Insert Into controlled_process (id, project_id, name, description)
    Values (0,'00112233-4455-6677-8899-aabbccddeeff','test controlled process','Das ist der Test controlled process');

Insert Into control_algorithm (id, project_id, name, description)
    Values (0,'123e4567-e89b-12d3-a456-426655440000','blub control algorithm','Das ist der Blub control algorithm');
Insert Into control_algorithm (id, project_id, name, description)
    Values (0,'00112233-4455-6677-8899-aabbccddeeff','test control algorithm','Das ist der Test control algorithm');

Insert Into process_variable (id, project_id, control_algorithm_id, name, description)
    Values (0,'123e4567-e89b-12d3-a456-426655440000', 0, 'blub process variable','Das ist die Blub process Variable');
Insert Into process_variable (id, project_id, control_algorithm_id, name, description)
    Values (0,'00112233-4455-6677-8899-aabbccddeeff', 0, 'blub process variable','Das ist die Blub process Variable');

Insert Into actuator (id, project_id, name, description)
    Values (0,'123e4567-e89b-12d3-a456-426655440000','blub actuator','Das ist der Blub actuator');
Insert Into actuator (id, project_id, name, description)
    Values (0,'00112233-4455-6677-8899-aabbccddeeff','blub actuator','Das ist der Test actuator');
	
Insert Into sensor (id, project_id, name, description)
    Values (0,'123e4567-e89b-12d3-a456-426655440000','blub sensor','Das ist der Blub sensor');
Insert Into sensor (id, project_id, name, description)
    Values (0,'00112233-4455-6677-8899-aabbccddeeff','blub sensor','Das ist der Test sensor');
	
Insert Into responsibility (id, project_id, system_constraint_id, name, description)
    Values (0,'123e4567-e89b-12d3-a456-426655440000', 0,'blub responsibility','Das ist Blub responsibility');
Insert Into responsibility (id, project_id, system_constraint_id, name, description)
    Values (0,'00112233-4455-6677-8899-aabbccddeeff', 0,'test responsibility','Das ist Test responsibility');