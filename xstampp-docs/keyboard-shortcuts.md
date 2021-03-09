#Keyboard Shortcut Documentation
The keyboard shortcuts were realized with Hotkeys.
Each matching component has been implemented in the constructor.
```Typescript

  subscriptions: Subscription[] = [];
constructor(
	...
	private readonly hotkeys: Hotkeys)
    {
    ...
  this.subscriptions.push(this.hotkeys.addShortcut({ keys: 'shift.control.randomkey', description: 'Random description' }).subscribe(() => {
      this.method();
    }));
    ...
     ngOnDestroy(): void {
    this.subscriptions.forEach((subscription: Subscription) => subscription.unsubscribe());
  }
 ```
Current keyboard shortcuts in the system are the following:
- **SHIFT + CONTROL + H:** Show all current shortcuts
- **SHIFT + CONTROLE + N:** New element from each entitiy in the system. For example new project, new loss,...
- **SHIFT + CONTROL + S:** Save changes in the deatail sheet
- **SHIFT + CONTROL + I:** Import a project in the project selection
- **SHIFT + CONTROL + E:** Import the example project in the project selection
- **SHIFT + CONTROL + C:** Copy an existing project in the project selection
- **SHIFT + CONTROL + BACKSPACE:** Cancel edit in the detail sheet
- **SHIFT + CONTROL + P:** Go to the project selection
- **SHIFT + CONTROL + R:** Register a new user at the login page
- **SHIFT + CONTROL + ARROW DOWN:** Switch down one element in the navigation
- **SHIFT + CONTROL + ARROW UP:** Switch up one element in the navigation
