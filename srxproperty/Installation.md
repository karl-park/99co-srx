# Installation

# Steps

1. Clone from repository
1. Open terminal (Mac OS) / command prompt (Windows), open the project folder, enter `npm install`.

## Manual Setup

### iOS & Android

1. Go to `node_modules > react-native-render-html > src > HTMLRenderers.js`.
    import { TouchableOpacity, Text, View, Platform, WebView } from 'react-native';
    *** delete WebView from 'react-native' and 
    import { WebView } from "react-native-webview";

### iOS only

#### A.
Go to file `RNNControllerFactory.m`, replace method

```

- (UIViewController *)createBottomTabs:(RNNLayoutNode*)node {
    ...
}

```

with 

```
- (UIViewController *)createBottomTabs:(RNNLayoutNode*)node {
    RNNLayoutInfo* layoutInfo = [[RNNLayoutInfo alloc] initWithNode:node];
    RNNNavigationOptions* options = [[RNNNavigationOptions alloc] initWithDict:node.data[@"options"]];
    RNNBottomTabsPresenter* presenter = [BottomTabsPresenterCreator createWithDefaultOptions:_defaultOptions];
    NSArray *childViewControllers = [self extractChildrenViewControllersFromNode:node];
    BottomTabPresenter* bottomTabPresenter = [BottomTabPresenterCreator createWithDefaultOptions:_defaultOptions children:childViewControllers];;
    RNNDotIndicatorPresenter* dotIndicatorPresenter = [[RNNDotIndicatorPresenter alloc] initWithDefaultOptions:_defaultOptions];
	BottomTabsBaseAttacher* bottomTabsAttacher = [_bottomTabsAttachModeFactory fromOptions:options];
    
    RNNBottomTabsController *bottomTabs = [RNNBottomTabsController alloc];
    
    UIStoryboard *board = [UIStoryboard storyboardWithName:@"SRXTabBarStoryboard" bundle:[NSBundle mainBundle]];
    if (board) {
        RNNBottomTabsController *controller = [board instantiateViewControllerWithIdentifier:@"SRXTabBarControllerID"];
        if (controller) {
            bottomTabs = controller;
        }
    }
    
    return [bottomTabs initWithLayoutInfo:layoutInfo
                                  creator:_creator
                                  options:options
                           defaultOptions:_defaultOptions
                                presenter:presenter
                       bottomTabPresenter:bottomTabPresenter
                    dotIndicatorPresenter:dotIndicatorPresenter
                             eventEmitter:_eventEmitter
                     childViewControllers:childViewControllers
                       bottomTabsAttacher:bottomTabsAttacher
            ];
}

```

#### B.

Go to `node_modules > react-native > Libraries > Image > RCTUIImageViewAnimated.m`.
    
    follow instruction 
    https://github.com/facebook/react-native/commit/b6ded7261544c703e82e8dbfa442dad4b201d428#


    - (void)displayLayer:(CALayer *)layer
    {
        if (_currentFrame) {
            layer.contentsScale = self.animatedImageScale;
            layer.contents = (__bridge id)_currentFrame.CGImage;
        } else {
            [super displayLayer:layer];
        }
    }




### Android only

#### A. 
Go to `node_modules > react-native-wheel-picker > WheelCurvedPicker.android`
    remove propTypes in wheelCurvedPicker and Item class

    add the below code outside of item class :
    WheelCurvedPicker.propTypes = {
     ...View.propTypes,
     data: PropTypes.array,
     textColor: ColorPropType,
     textSize: PropTypes.number,
     itemStyle: PropTypes.object,
     itemSpace: PropTypes.number,
     onValueChange: PropTypes.func,
     selectedValue: PropTypes.any,
     selectedIndex: PropTypes.number,
}

Item.propTypes = {
     value: PropTypes.any, // string or integer basically
     label: PropTypes.string,

#### B.  

Go to `node_modules > react-native-wheel-picker > android > src > main > java > com > zyu > ReactWheelCurvedPickerManager.java`

    1. replace method `createViewInstance`

    @Override
    protected ReactWheelCurvedPicker createViewInstance(ThemedReactContext reactContext) {
        ReactWheelCurvedPicker picker = new ReactWheelCurvedPicker(reactContext);
        picker.setTextColor(Color.parseColor("#858585"));
        picker.setCurrentTextColor(Color.parseColor("#619dfd"));
        picker.setTextSize(DEFAULT_TEXT_SIZE);
        picker.setItemSpace(DEFAULT_ITEM_SPACE);

        return picker;
    }

    2. replace method `setData`

    @ReactProp(name="data")
    public void setData(ReactWheelCurvedPicker picker, ReadableArray items) {
        if (picker != null) {
            ArrayList<String> valueData = new ArrayList<>();
            ArrayList<String> labelData = new ArrayList<>();
            for (int i = 0; i < items.size(); i ++) {
                ReadableMap itemMap = items.getMap(i);
                valueData.add(itemMap.getString("value"));
                labelData.add(itemMap.getString("label"));
            }
            picker.setValueData(valueData);
            picker.setData(labelData);
        }
    }  

#### C.  
Go to `node_modules > react-native-wheel-picker > android > src > main > java > com > zyu > ReactWheelCurvedPicker.java`  
   1. Under `public class ReactWheelCurvedPicker extends WheelCurvedPicker`  
      replace `private List<Integer> mValueData;` with `private List<String> mValueData;`  
   
   2. Replace class `ItemSelectedEvent`  

      class ItemSelectedEvent extends Event<ItemSelectedEvent> {  
          
          public static final String EVENT_NAME = "wheelCurvedPickerPageSelected";

          private final String mValue;

          protected ItemSelectedEvent(int viewTag,  String value) {
              super(viewTag);
              mValue = value;
          }

          @Override
          public String getEventName() {
              return EVENT_NAME;
          }

          @Override
          public void dispatch(RCTEventEmitter rctEventEmitter) {
              rctEventEmitter.receiveEvent(getViewTag(), getEventName(), serializeEventData());
          }

          private WritableMap serializeEventData() {
              WritableMap eventData = Arguments.createMap();
              eventData.putString("data", mValue);
              return eventData;
          }
      }

#### D.  

Go to `node_modules > react-native-maps > android > build.gradle`  
    Update  

    def DEFAULT_GOOGLE_PLAY_SERVICES_VERSION = "16.1.0"


### Before react-native version 0.60 bug fixes that may occur after upgrade

### Android only

#### A.

Go to `node_modules > react-native-navigation > lib > android > app > src > main > java > com > reactnativenavigation > presentation > SideMenuPresenter`  
and update the following code
This changes is to prevent side menu showing when `enabled` in sideMenu is set to `false`.

Replace method `mergeVisibility` & `mergeLockMode`.

    private void mergeVisibility(SideMenuRootOptions options) {
        if (options.left.enabled.isFalse() == false) {
            sideMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);
        } else if (options.left.enabled.isTrue()) {
            sideMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);
        }

        if (options.right.enabled.isFalse() == false) {
            sideMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        } else if (options.right.enabled.isTrue()) {
            sideMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);
        }

        if (options.left.visible.isTrue()) {
            sideMenu.openDrawer(Gravity.LEFT);
        } else if (options.left.visible.isFalse() == false && sideMenu.isDrawerOpen(Gravity.LEFT)) {
            sideMenu.closeDrawer(Gravity.LEFT);
        }
        if (options.right.visible.isTrue()) {
            sideMenu.openDrawer(Gravity.RIGHT);
        } else if (options.right.visible.isFalse() == false && sideMenu.isDrawerOpen(Gravity.RIGHT)) {
            sideMenu.closeDrawer(Gravity.RIGHT);
        }

        if (options.left.visible.isTrue()) {
            sideMenu.openDrawer(Gravity.LEFT, options.left.animate.get(true));
        } else if (options.left.visible.isFalse()) {
            sideMenu.closeDrawer(Gravity.LEFT, options.left.animate.get(true));
        }

        if (options.right.visible.isTrue()) {
            sideMenu.openDrawer(Gravity.RIGHT, options.right.animate.get(true));
        } else if (options.right.visible.isFalse()) {
            sideMenu.closeDrawer(Gravity.RIGHT, options.right.animate.get(true));
        }
    }

    private void mergeLockMode(SideMenuRootOptions options) {
        if (options.left.enabled.isFalse() == false) {
            sideMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);
        } else if (options.left.enabled.isTrue()) {
            sideMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);
        }

        if (options.right.enabled.isFalse() == false) {
            sideMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        } else if (options.right.enabled.isTrue()) {
            sideMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);
        }

        if (options.left.visible.isTrue()) {
            sideMenu.openDrawer(Gravity.LEFT);
        } else if (options.left.visible.isFalse() == false && sideMenu.isDrawerOpen(Gravity.LEFT)) {
            sideMenu.closeDrawer(Gravity.LEFT);
        }
        if (options.right.visible.isTrue()) {
            sideMenu.openDrawer(Gravity.RIGHT);
        } else if (options.right.visible.isFalse() == false && sideMenu.isDrawerOpen(Gravity.RIGHT)) {
            sideMenu.closeDrawer(Gravity.RIGHT);
        }
        if (options.left.enabled.isFalse()) {
            sideMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);
        } else if (options.left.enabled.isTrue()) {
            sideMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);
        }

        if (options.right.enabled.isFalse()) {
            sideMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        } else if (options.right.enabled.isTrue()) {
            sideMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);
        }
    }  


#### C.
If there is any issue related to "Lottie" after built and run, please check:
i) Podfile: 
  'lottie-ios' 'lottie-react-native' are both commented/ remove
ii) Frameworks, Libraries and Embedded Content section of XCode:
libLottie.a and libLottieReactnative.a are added
if both files are not added, please drag and drop both files from node_modules under "lottie-ios" and "lottie-react-native/src/ios" for respective xcodeproj file to libraries and add libLottie.a and libLottieReactnative.a into Frameworks, Libraries and Embedded Content section

#### D. 
realm
    if doing remote-debugging and encounter error message: Failed to execute 'send' on 'XMLHttpRequest': Failed to load 'http://192.168.1.178:8082/create_session'.
    Android:
    1) "node_modules/realm/lib/browser/rpc.js"
    a) Replace 
            if (global.__debug__) {
            let request = global.__debug__.require('sync-request');
            let response = request('POST', url, {
            body: JSON.stringify(data),
            headers: {
                "Content-Type": "text/plain;charset=UTF-8"
            }
            });

            statusCode = response.statusCode;
            responseText = response.body.toString('utf-8');
            } else {
                let body = JSON.stringify(data);
                let request = new XMLHttpRequest();

                request.open('POST', url, false);
                request.send(body);

                statusCode = request.status;
                responseText = request.responseText;
            }
        to
            if (global.__debug__) {
            let request = global.__debug__.require('sync-request');
            let response = request('POST', url, {
            body: JSON.stringify(data),
            headers: {
                "Content-Type": "text/plain;charset=UTF-8"
            }
            });

            statusCode = response.statusCode;
            responseText = response.body.toString('utf-8');
            } else {
                let body = JSON.stringify(data);
                let request = new XMLHttpRequest();

                // ALWAYS POINT TO LOCALHOST
                if (__DEV__) {
                    url = 'http://localhost:8083' + url.substring(url.lastIndexOf('/'));
                }

                request.open('POST', url, false);
                request.send(body);

                statusCode = request.status;
                responseText = request.responseText;
            }
    b) if the issue persist, change http://localhost:8083 to http://localhost:8082 
    ios: 
    1) "node_modules/realm/react-native/ios/RealmReact/RealmReact.mm"
    a) Replace the line [response setValue:@"http://localhost:8081" forAdditionalHeader:@"Access-Control-Allow-Origin"];
       to [response setValue:@"*" forAdditionalHeader:@"Access-Control-Allow-Origin"];


#### E.
Go to `node_modules > react-native-navigation > lib > android > app > src > main > java > com > reactnativenavigation > NavigationActivity
In NavigationActivity file, replace "AppCompactActivity" with "ReactActivity"
