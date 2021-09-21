# Usage: Create `example.SomethingTransactionDetailsActivity` with corresponding view model and layout
# Input:
# Argument 1: package name, e.g. `example`
# Argument 2: Class name, e.g. for `SomethingTransactionDetailsActivity`, it is `SomethingTransactionDetails` (in TitleCase)

# Example call:
# # cd agent-connect-android/scaffold
# # zsh generate_scaffold.sh example SomethingTransactionDetails

# TODO Add activity entry into AndroidManifest
# TODO Import required classes into activity

PACKAGE_NAME="$1"

ACTIVITY_NAME="$2"

# TODO Refactor `convertTitleToCamelCase` into separate module
# The first letter of ACTIVITY_NAME to lower case
FIRST_LOWER_CASE=`echo "${ACTIVITY_NAME:0:1}" | sed 'y/ABCDEFGHIJKLMNOPQRSTUVWXYZ/abcdefghijklmnopqrstuvwxyz/'`

ACTIVITY_NAME_CAMEL=`echo $ACTIVITY_NAME | sed "s/^[ABCDEFGHIJKLMNOPQRSTUVWXYZ]/$FIRST_LOWER_CASE/1"`

ACTIVITY_NAME_SNAKE=`echo $ACTIVITY_NAME | sed 's/\([a-z0-9]\)\([A-Z]\)/\1_\2/g' | sed 'y/ABCDEFGHIJKLMNOPQRSTUVWXYZ/abcdefghijklmnopqrstuvwxyz/'`

# Root directories
PROJECT_ROOT="../app/src/main/java/sg/searchhouse/agentconnect"
ACTIVITY_ROOT="$PROJECT_ROOT/view/activity"
VIEW_MODEL_ROOT="$PROJECT_ROOT/viewmodel/activity"
VIEW_MODEL_COMPONENT_ROOT="$PROJECT_ROOT/dependency/component"

# Root directories but class form
PROJECT_ROOT_2="sg.searchhouse.agentconnect"
VIEW_MODEL_ROOT_2="${PROJECT_ROOT_2}.viewmodel.activity"

# Directories
VIEW_MODEL_DIR="$VIEW_MODEL_ROOT/$PACKAGE_NAME"
ACTIVITY_DIR="$ACTIVITY_ROOT/$PACKAGE_NAME"
LAYOUT_DIR="../app/src/main/res/layout"

# Classes
VIEW_MODEL_CLASS="${ACTIVITY_NAME}ViewModel"
ACTIVITY_CLASS="${ACTIVITY_NAME}Activity"
BINDING_CLASS="Activity${ACTIVITY_NAME}Binding"

# Classes in camel case
VIEW_MODEL_CLASS_CAMEL="${ACTIVITY_NAME_CAMEL}ViewModel"

# Full path
VIEW_MODEL_PATH="$VIEW_MODEL_DIR/${VIEW_MODEL_CLASS}"

# Full path but class form
VIEW_MODEL_PATH_2="${VIEW_MODEL_ROOT_2}.${PACKAGE_NAME}.${VIEW_MODEL_CLASS}"

# Files
VIEW_MODEL_FILE="$VIEW_MODEL_DIR/${VIEW_MODEL_CLASS}.kt"
VIEW_MODEL_COMPONENT_FILE="$VIEW_MODEL_COMPONENT_ROOT/ViewModelComponent.kt"
LAYOUT_FILE="$LAYOUT_DIR/activity_${ACTIVITY_NAME_SNAKE}.xml"
ACTIVITY_FILE="$ACTIVITY_DIR/${ACTIVITY_CLASS}.kt"

# Placeholders
ACTIVITY_NAME_PLACEHOLDER="(ACTIVITY_NAME)"
VIEW_MODEL_NAME_PLACEHOLDER="(VIEW_MODEL_NAME)"
PACKAGE_NAME_PLACEHOLDER="(PACKAGE_NAME)"
VIEW_MODEL_PATH_PLACEHOLDER="(VIEW_MODEL_PATH)"
BINDING_NAME_PLACEHOLDER="(BINDING_NAME)"
LAYOUT_NAME_PLACEHOLDER="(LAYOUT_NAME)"

createViewHolder() {
	# Create view model folder if not exist
	mkdir -p $VIEW_MODEL_DIR

	# Copy file
	cp scaffold_view_model.txt $VIEW_MODEL_FILE

	# Replace placeholders
	sed -i -e "s/$ACTIVITY_NAME_PLACEHOLDER/$ACTIVITY_NAME/g" $VIEW_MODEL_FILE
	sed -i -e "s/$PACKAGE_NAME_PLACEHOLDER/$PACKAGE_NAME/g" $VIEW_MODEL_FILE

	# Remove redundant `-e` file generated from replace placeholders
	rm -f "${VIEW_MODEL_FILE}-e"

	injectViewModelComponent
}

injectViewModelComponent() {
	# Add import
	IMPORT_FIND="package ${PROJECT_ROOT_2}.dependency.component"
	IMPORT_REPLACE="$IMPORT_FIND import ${VIEW_MODEL_ROOT_2}.${PACKAGE_NAME}.${VIEW_MODEL_CLASS}"

	# Add declare inject
	INJECT_FIND="}$"
	INJECT_REPLACE="    fun inject($VIEW_MODEL_CLASS_CAMEL: $VIEW_MODEL_CLASS)}"

	sed -i -e "s/$IMPORT_FIND/$IMPORT_REPLACE/1" $VIEW_MODEL_COMPONENT_FILE
	sed -i -e "s/$INJECT_FIND/$INJECT_REPLACE/" $VIEW_MODEL_COMPONENT_FILE

	# Remove redundant file
	rm -f "${VIEW_MODEL_COMPONENT_FILE}-e"
}

createLayout() {
	# Copy file
	cp scaffold_layout.txt $LAYOUT_FILE

	# Replace placeholders
	sed -i -e "s/$VIEW_MODEL_PATH_PLACEHOLDER/$VIEW_MODEL_PATH_2/g" $LAYOUT_FILE

	# TODO Create string resource for activity title

	# Remove redundant `-e` file generated from replace placeholders
	rm -f "${LAYOUT_FILE}-e"
}

createActivity() {
	# Create activity folder if not exist
	mkdir -p $ACTIVITY_DIR

	# Copy file
	cp scaffold_activity.txt $ACTIVITY_FILE

	# Replace placeholders
	sed -i -e "s/$PACKAGE_NAME_PLACEHOLDER/$PACKAGE_NAME/g" $ACTIVITY_FILE
	sed -i -e "s/$ACTIVITY_NAME_PLACEHOLDER/$ACTIVITY_CLASS/g" $ACTIVITY_FILE
	sed -i -e "s/$VIEW_MODEL_NAME_PLACEHOLDER/$VIEW_MODEL_CLASS/g" $ACTIVITY_FILE
	sed -i -e "s/$BINDING_NAME_PLACEHOLDER/$BINDING_CLASS/g" $ACTIVITY_FILE
	sed -i -e "s/$LAYOUT_NAME_PLACEHOLDER/$ACTIVITY_NAME_SNAKE/g" $ACTIVITY_FILE

	# TODO Import classes

	# Remove redundant `-e` file generated from replace placeholders
	rm -f "${ACTIVITY_FILE}-e"

	# TODO Add activity to manifest
}

createViewHolder
createLayout
createActivity