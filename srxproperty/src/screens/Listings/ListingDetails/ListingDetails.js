import React, {Component} from 'react';
import {
  Image,
  Platform,
  Share,
  View,
  Alert,
  TouchableWithoutFeedback,
  Dimensions,
  ActivityIndicator,
  Linking,
  Animated,
} from 'react-native';
import {AmenitiesSource} from '../../Amenities/constant';
import SafeAreaView from 'react-native-safe-area-view';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import {connect} from 'react-redux';
import {shortlistListing, removeShortlist} from '../../../actions';
import {Placeholder_Agent, AppTopBar_BackBtn} from '../../../assets';
import {
  Accordion,
  Button,
  FeatherIcon,
  FontAwesomeIcon,
  Separator,
  Heading2,
  EnquirySheet,
  EnquirySheetSource,
  BodyText,
} from '../../../components';
import {
  SRXColor,
  AppConstant,
  IS_IOS,
  IS_IPHONE_X,
  ListingDetailsViewingItems,
  AlertMessage,
  LeadTypes,
  LeadSources
} from "../../../constants";
import {
  AgentPO,
  ListingDetailPO,
  ListingPO,
  ListingPhotoPO
} from "../../../dataObject";
import { ChatService, SearchPropertyService, TrackingService } from "../../../services";
import { Spacing, TopBarConstants, AppTheme } from "../../../styles";
import {
  ObjectUtil,
  ShortlistUtil,
  PropertyTypeUtil,
  GoogleAnalyticUtil,
  DebugUtil,
} from '../../../utils';
import {
  AgentInformation,
  Amenities,
  BasicInformation,
  Description,
  Facilities,
  KeyInformation,
  ListingPhotos,
  MortgageCalculator,
  PostingInformation,
} from './DetailsContent';
import {AccordianHeader, CallToActionBar} from './DetailsComponent';
import {ListingsCardPreview} from '../ListingsPreview';
import {XTrend} from '../../X-Value';
import {WebView} from 'react-native-webview';

var {height, width} = Dimensions.get('window');

const SECTION_TYPE = {
  Photo: 'Photo',
  BasicInfo: 'Basic Info',
  PostingInfo: 'Posting Information',
  KeyInformation: 'Key Information',
  Description: 'Description',
  Facilities: 'Facilities',
  Amenities: 'Amenities',
  LocationMap: 'Location Map', //temporary section
  HomeValue: 'X-Value',
  MortgageCal: 'Mortgage Calculator',
  RecommendedListings: 'Recommended Listings',
  ContactAgent: 'Contact Agent',
};

const TransparentTopBarStyle = {
  background: {
    color: 'transparent',
  },
  backButton: {
    color: AppTheme.topBarBackButtonColor,
    icon: AppTopBar_BackBtn,
    title: '',
  },
  leftButtonColor: SRXColor.Teal,
  rightButtonColor: SRXColor.Teal,
  elevation: 0,
};

const ColorTopBarStyle = {
  background: {
    color: SRXColor.White,
  },
  backButton: {
    color: AppTheme.topBarBackButtonColor,
    icon: AppTopBar_BackBtn,
    title: '',
  },
  /**
   * left & right button setting in mergeOptions method has no effect,
   * to change the color, replace the buttons
   */
  leftButtonColor: SRXColor.Teal,
  rightButtonColor: SRXColor.Teal,
  elevation: 4,
};

const LoadingState = {
  Normal: 'normal',
  Loading: 'loading',
  Loaded: 'loaded',
};

const Error = {
  NotExitListing: 'The listing does not exist.',
};

class ListingDetails extends Component {
  static options(passProps) {
    return {
      bottomTabs: {
        visible: false,
        animate: true,
        drawBehind: IS_IOS ? false : true,
      },
      topBar: {
        visible: true,
        drawBehind: true,
        ...TransparentTopBarStyle,
      },
    };
  }

  static propTypes = {
    listingId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  };

  state = {
    conversationId: null,
    sections: [],
    activeSections: [0, 1, 2, 3],
    recommendedListings: [], //if no result from server, it will become null
    loadingState: LoadingState.Normal,
    fadeAnim: new Animated.Value(0),
  };

  currentY = 0;
  postInfoY = -1; //starting with -1, if rendered set to value >0

  currentTopBarStyle = TransparentTopBarStyle;

  addRightButtons(tintColor) {
    const componentId = this.props.componentId;
    const isShortlisted = ShortlistUtil.isShortlist({
      listingId: this.props.listingId,
    });
    const extraSetting = {};
    if (tintColor) {
      extraSetting.color = tintColor;
    }
    return new Promise(function(resolve, reject) {
      Promise.all([
        FontAwesomeIcon.getImageSource(
          isShortlisted ? 'heart' : 'heart-o',
          24,
          SRXColor.White,
        ),
        FeatherIcon.getImageSource(
          IS_IOS ? 'share' : 'share-2',
          24,
          SRXColor.White,
        ),
      ])
        .then(values => {
          Navigation.mergeOptions(componentId, {
            topBar: {
              rightButtons: [
                {
                  id: 'listingDetail_ShortlistBtn',
                  icon: values[0],
                  ...extraSetting,
                },
                {
                  id: 'listingDetail_ShareBtn',
                  icon: values[1],
                  ...extraSetting,
                },
              ],
            },
          });
          resolve(true);
        })
        .catch(error => {
          console.log(error);
          reject(error);
        })
        .done();
    });
  }

  constructor(props) {
    super(props);
    Navigation.events().bindComponent(this); // <== Will be automatically unregistered when unmounted

    this.onTrendEndLoading = this.onTrendEndLoading.bind(this);
    this.onEnquiriesSubmitted = this.onEnquiriesSubmitted.bind(this);
    this.viewAgentCV = this.viewAgentCV.bind(this);
    this.onActiveSectionsChanged = this.onActiveSectionsChanged.bind(this);
    this.whatsAppAgent = this.whatsAppAgent.bind(this);

    this.onScroll = this.onScroll.bind(this);
  }

  componentDidMount() {
    const {listingId, refType} = this.props;
    this.setState({loadingState: LoadingState.Loading}, () => {
      this.loadListingDetails({listingId, refType});
    });
  }

  componentDidUpdate(prevProps, prevState) {
    if (prevProps.shortlistData != this.props.shortlistData) {
      this.addRightButtons(this.currentTopBarStyle.rightButtonColor);
    }
  }

  navigationButtonPressed({buttonId}) {
    if (buttonId === 'listingDetail_ShortlistBtn') {
      this.shortlistClicked();
    } else if (buttonId === 'listingDetail_ShareBtn') {
      this.shareListing();
    }
  }

  onScroll = ({
    nativeEvent: {
      contentOffset: {x, y},
    },
  }) => {
    this.currentY = y;
    const imageBottomY =
      (width / 375) * 285 -
      (IS_IPHONE_X
        ? TopBarConstants.statusBarHeight + TopBarConstants.topBarHeight
        : 0);
    if (y > imageBottomY) {
      if (this.currentTopBarStyle != ColorTopBarStyle) {
        Navigation.mergeOptions(this.props.componentId, {
          topBar: ColorTopBarStyle,
        });
        this.addRightButtons(ColorTopBarStyle.rightButtonColor);
        this.currentTopBarStyle = ColorTopBarStyle;
      }
    } else {
      if (this.currentTopBarStyle != TransparentTopBarStyle) {
        Navigation.mergeOptions(this.props.componentId, {
          topBar: TransparentTopBarStyle,
        });
        this.addRightButtons(TransparentTopBarStyle.rightButtonColor);
        this.currentTopBarStyle = TransparentTopBarStyle;
      }
    }

    if (y > 450) {
      Animated.timing(this.state.fadeAnim, {
        toValue: 1,
        duration: 100,
        useNativeDriver: true,
      }).start();
    } else {
      Animated.timing(this.state.fadeAnim, {
        toValue: 0,
        duration: 100,
        useNativeDriver: true,
      }).start();
    }
  };

  onActiveSectionsChanged = newActive => {
    //This method is called only when user toggle the section
    //manually setting this.state.activeSections will not trigger
    //unless there is changes in the library
    const {activeSections} = this.state;

    this.analyticForNewActiveSection(activeSections, newActive);

    this.setState({activeSections: newActive});
  };

  analyticForNewActiveSection(originalActive, newActive) {
    const {sections} = this.state;

    if (newActive.length > originalActive.length) {
      const newSections = newActive.filter(x => !originalActive.includes(x));
      console.log(newSections);
      newSections.forEach(item => {
        const sectionType = sections[item];
        console.log(sectionType);
        switch (sectionType) {
          case SECTION_TYPE.Facilities:
            GoogleAnalyticUtil.trackListingDetailsUserActions({
              viewingItem: ListingDetailsViewingItems.facilities,
            });
            break;
          case SECTION_TYPE.HomeValue:
            GoogleAnalyticUtil.trackListingDetailsUserActions({
              viewingItem: ListingDetailsViewingItems.homeValueEstimator,
            });
            break;
          case SECTION_TYPE.MortgageCal:
            GoogleAnalyticUtil.trackListingDetailsUserActions({
              viewingItem: ListingDetailsViewingItems.mortgageCalculator,
            });
            break;
          case SECTION_TYPE.RecommendedListings:
            GoogleAnalyticUtil.trackListingDetailsUserActions({
              viewingItem: ListingDetailsViewingItems.similarLiistings,
            });
            break;
          case SECTION_TYPE.ContactAgent:
            GoogleAnalyticUtil.trackListingDetailsUserActions({
              viewingItem: ListingDetailsViewingItems.contactAnAgent,
            });
            break;
          default:
            break;
        }
      });
    }
  }

  loadListingDetails = ({listingId, refType}) => {
    const {userPO} = this.props;
    if (listingId) {
      SearchPropertyService.loadFullListingDetails({listingId, refType})
        .then(response => {
          if (!ObjectUtil.isEmpty(response)) {
            const {listing} = response;
            if (!ObjectUtil.isEmpty(listing)) {
              const sections = [SECTION_TYPE.Photo];
              const activeSections = [0];

              const listingDetailPO = new ListingDetailPO(
                listing.listingDetailPO,
              );

              const listingMedias = [];
              if (
                !ObjectUtil.isEmpty(listing.listingPhotoPO) &&
                !ObjectUtil.isEmpty(listing.listingPhotoPO.listingImageLinks)
              ) {
                const {listingImageLinks} = listing.listingPhotoPO;
                listingImageLinks.map(item => {
                  listingMedias.push(new ListingPhotoPO(item));
                });
              }

              const {listingPO} = listingDetailPO;
              if (!ObjectUtil.isEmpty(listingPO)) {
                //include basic info
                sections.push(SECTION_TYPE.BasicInfo);
                activeSections.push(sections.indexOf(SECTION_TYPE.BasicInfo));

                //posting info
                sections.push(SECTION_TYPE.PostingInfo);
                activeSections.push(sections.indexOf(SECTION_TYPE.PostingInfo));

                //include key information
                sections.push(SECTION_TYPE.KeyInformation);
                activeSections.push(
                  sections.indexOf(SECTION_TYPE.KeyInformation),
                );

                //include description
                if (!ObjectUtil.isEmpty(listingPO.remarks)) {
                  sections.push(SECTION_TYPE.Description);
                  activeSections.push(
                    sections.indexOf(SECTION_TYPE.Description),
                  );
                }

                //include Location Mao
                sections.push(SECTION_TYPE.LocationMap);
                activeSections.push(sections.indexOf(SECTION_TYPE.LocationMap));

                //include Facilities
                sections.push(SECTION_TYPE.Facilities);

                //include Amenities
                // sections.push(SECTION_TYPE.Amenities);

                //include Home Value Calculator
                if (
                  !PropertyTypeUtil.isCommercial(listingPO.cdResearchSubType)
                ) {
                  sections.push(SECTION_TYPE.HomeValue);
                }

                if (!ObjectUtil.isEmpty(listingPO) && listingPO.type === 'S') {
                  //include Mortgage Calculator
                  sections.push(SECTION_TYPE.MortgageCal);
                }

                //include recommended listings
                sections.push(SECTION_TYPE.RecommendedListings);

                //include contact agent
                sections.push(SECTION_TYPE.ContactAgent);
                activeSections.push(
                  sections.indexOf(SECTION_TYPE.ContactAgent),
                );
              }

              this.setState(
                {
                  listingDetailPO,
                  agentPO: new AgentPO(listing.agentPO),
                  listingMedias,
                  sections,
                  activeSections,
                  loadingState: LoadingState.Loaded,
                },
                () => {
                  if (!ObjectUtil.isEmpty(userPO)) {
                    this.findConversationId();
                  }
                  this.addRightButtons(
                    this.currentTopBarStyle.rightButtonColor,
                  );
                  this.loadRecommendedListings({listingId, refType});
                },
              );
            }
          }
        })
        .catch(error => {
          const {data} = error;
          let loadingState;
          if (data.Error === Error.NotExitListing) {
            loadingState = LoadingState.Loaded;
          } else {
            loadingState = LoadingState.Normal;
          }
          this.setState({loadingState: loadingState});
        });
    }
  };

  findConversationId = () => {
    const {agentPO} = this.state;
    const userIdList = [agentPO.userId.toString()];
    ChatService.findConversationIds({
      otherUserIds: JSON.stringify(userIdList),
    }).then(response => {
      console.log(response);
      if (!ObjectUtil.isEmpty(response)) {
        const {conversationIds} = response;
        if (!ObjectUtil.isEmpty(conversationIds)) {
          let conversationIdList = Object.values(conversationIds);
          this.setState({conversationId: conversationIdList[0]});
        }
      }
    });
  };

  loadRecommendedListings = ({listingId, refType}) => {
    if (listingId) {
      SearchPropertyService.loadRecommendedListings({listingId, refType})
        .then(response => {
          const newRecommendedListings = [];
          if (!ObjectUtil.isEmpty(response)) {
            if (!ObjectUtil.isEmpty(response)) {
              const {result} = response;
              if (!ObjectUtil.isEmpty(result) && Array.isArray(result)) {
                result.map(item => {
                  newRecommendedListings.push(new ListingPO(item));
                });
              }
            }
          }

          if (newRecommendedListings.length > 0) {
            this.setState({
              recommendedListings: newRecommendedListings,
            });
          } else {
            this.setState({
              recommendedListings: null,
            });
            this.removeSectionOfContent(SECTION_TYPE.RecommendedListings);
          }
        })
        .catch(error => {
          console.log(error);
          this.setState({
            recommendedListings: null,
          });
          this.removeSectionOfContent(SECTION_TYPE.RecommendedListings);
        });
    }
  };

  removeSectionOfContent(content) {
    const {sections, activeSections} = this.state;
    const index = sections.indexOf(content);
    if (index >= 0) {
      const newSections = [...sections];
      newSections.splice(index, 1);
      const newActiveSections = [];

      activeSections.map(item => {
        if (item < index) {
          newActiveSections.push(item);
        } else if (item > index && item - 1 < newSections.length) {
          newActiveSections.push(item - 1);
        }
      });

      this.setState({
        sections: newSections,
        activeSections: newActiveSections,
      });
    }
  }

  shareListing = async () => {
    const {listingDetailPO} = this.state;
    let msg =
      'Hey there! Check out this property I found on SRX… what do you think?😊';
    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const {listingPO} = listingDetailPO;
      if (!ObjectUtil.isEmpty(listingPO)) {
        const url = await listingPO.getListingURL();
        if (!ObjectUtil.isEmpty(url)) {
          msg = msg + ' ' + url;
        }
      }
    }

    const content = {
      message: msg,
      // url: listingURL //iOS only //having message +  url will
    };
    const options = {
      dialogTitle: 'Share Listing', //iOS only
    };

    Share.share(content, options);
  };

  shortlistClicked = () => {
    const isShortlisted = ShortlistUtil.isShortlist({
      listingId: this.props.listingId,
    });
    if (isShortlisted) {
      const shortlistPO = ShortlistUtil.getShortlistPOWithListingId({
        listingId: this.props.listingId,
      });
      if (!ObjectUtil.isEmpty(shortlistPO)) {
        const {id} = shortlistPO;
        this.props.removeShortlist({shortlistId: id});
      }
    } else {
      const {listingDetailPO, agentPO} = this.state;
      if (!ObjectUtil.isEmpty(listingDetailPO)) {
        const {listingPO} = listingDetailPO;
        if (!ObjectUtil.isEmpty(listingPO)) {
          if (ObjectUtil.isEmpty(listingPO.agentPO)) {
            listingPO.agentPO = agentPO;
          }
          this.props.shortlistListing({
            encryptedListingId: listingPO.encryptedId,
            listingType: listingPO.listingType,
            listingPO: listingPO,
          });
        }
      }
    }
  };

  enquiryClicked = () => {
    const {listingDetailPO, agentPO, conversationId} = this.state;
    const passData = {
      enquiryCallerComponentId: this.props.componentId,
      onEnquirySubmitted: this.onEnquiriesSubmitted,
    };

    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const {listingPO} = listingDetailPO;
      {
        if (!ObjectUtil.isEmpty(listingPO)) {
          if (ObjectUtil.isEmpty(listingPO.agentPO)) {
            listingPO.agentPO = agentPO;
          }
          passData.listingPO = listingPO;
        }
      }
    }

    if (conversationId != null) {
      passData.conversationId = conversationId;
    }

    /*
     * if listingPO is provided, this agentPO will not be used
     */
    if (ObjectUtil.isEmpty(passData.listingPO)) {
      passData.agentPO = agentPO;
    }

    //source
    passData.source = EnquirySheetSource.ListingDetails;

    EnquirySheet.show(passData);
  };

  onEnquiriesSubmitted() {
    let message = 'Your enquiry has been submitted.';

    Alert.alert(AlertMessage.SuccessMessageTitle, message);
  }

  showListingDetails = ({listingPO}) => {
    if (!ObjectUtil.isEmpty(listingPO)) {
      Navigation.push(this.props.componentId, {
        component: {
          name: 'PropertySearchStack.ListingDetails',
          passProps: {
            listingId: listingPO.getListingId(),
            refType: listingPO.listingType,
          },
        },
      });
    }
  };

  viewAgentCV() {
    const {componentId} = this.props;
    const {agentPO} = this.state;

    if (!ObjectUtil.isEmpty(agentPO)) {
      Navigation.push(componentId, {
        component: {
          name: 'ConciergeStack.AgentCV',
          passProps: {
            agentUserId: agentPO.getAgentId(),
          },
        },
      });
    }
  }

  onTrendEndLoading({success}) {
    if (!success) {
      this.removeSectionOfContent(SECTION_TYPE.HomeValue);
    }
  }

  whatsAppAgent() {
    const {agentPO, listingDetailPO} = this.state;
    GoogleAnalyticUtil.trackLeads({
      leadType: LeadTypes.whatsapp,
      source: LeadSources.listingDetails,
    });
    this.trackContactAgentActionsForListing('W');
    if (
      !ObjectUtil.isEmpty(agentPO) &&
      !ObjectUtil.isEmpty(agentPO.getMobileNumber())
    ) {
      var url =
        'https://api.whatsapp.com/send?phone=65' + agentPO.getMobileNumber(); //+"&text=xxx"

      var msgTemplate = '';
      if (!ObjectUtil.isEmpty(listingDetailPO)) {
        const { listingPO } = listingDetailPO;
        if (!ObjectUtil.isEmpty(listingPO)) {
          msgTemplate = listingPO.getSMSMessageTemplate();
        }
      }

      url += '&text=' + msgTemplate;

      Linking.canOpenURL(url)
        .then(supported => {
          if (!supported) {
            console.log("EnquirySheet - whatsapp - Can't handle url: " + url);
          } else {
            return Linking.openURL(url);
          }
        })
        .catch(err =>
          console.error(
            'EnquirySheet - An error occurred - whatsapp agent - ',
            err,
          ),
        );
    }
  }

  trackContactAgentActionsForListing(type) {
    /**
     * type
     * C - call, S - Sms, W - whatsapp
     */
    const {listingDetailPO} = this.state;
    if (
      !ObjectUtil.isEmpty(listingDetailPO) &&
      !ObjectUtil.isEmpty(listingDetailPO.listingPO) &&
      !ObjectUtil.isEmpty(listingDetailPO.listingPO.getListingId())
    ) {
      TrackingService.trackListingCallSMSOrWhatsapp({
        listingId: listingDetailPO.listingPO.getListingId(),
        type,
      }); //no need response
    }
  }

  //rendering view
  renderListingImages(sectionIndex) {
    const {listingMedias, listingDetailPO} = this.state;
    var droneViews = [];
    var virtualTours = [];
    var videoPOs = [];
    var tempListingPO = null;
    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const {listingPO} = listingDetailPO;
      if (!ObjectUtil.isEmpty(listingPO)) {
        droneViews = listingPO.getDroneViews();
        if (listingPO.hasVirtualTour()) {
          virtualTours.push(listingPO.getVirtualTour());
        }
        if (listingPO.hasVideoPOs()) {
          videoPOs = listingPO.getVideoPOs();
        }
        // get listingPO from listingDetailPO
        tempListingPO = listingPO;
      }
    }
    return (
      <ListingPhotos
        listingPO={tempListingPO}
        listingMedias={listingMedias}
        droneViews={droneViews}
        virtualTours={virtualTours}
        videoPOs={videoPOs}
      />
    );
  }

  renderBasicInformation() {
    const {listingDetailPO} = this.state;
    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const {listingPO} = listingDetailPO;
      if (!ObjectUtil.isEmpty(listingPO)) {
        return (
          <BasicInformation
            listingPO={listingPO}
            style={{backgroundColor: SRXColor.White}}
          />
        );
      }
    }
  }

  renderDescription() {
    const { listingDetailPO, agentPO } = this.state;
    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const {listingPO} = listingDetailPO;
      if (!ObjectUtil.isEmpty(listingPO)) {
        return (
          <Description
            description={listingPO.remarks}
            onMaskMobilePress={this.whatsAppAgent}
            style={{ backgroundColor: SRXColor.White }}
          />
        );
      }
    }
  }

  renderFacilities() {
    const {listingDetailPO} = this.state;
    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const {facilities, features, fixtures} = listingDetailPO;
      return (
        <Facilities
          style={{backgroundColor: SRXColor.White}}
          facilities={facilities || []}
          features={features || []}
          fixtures={fixtures || []}
        />
      );
    }
  }

  renderPostingInformation() {
    const {listingDetailPO} = this.state;
    if (!ObjectUtil.isEmpty(listingDetailPO.listingPO)) {
      return (
        <PostingInformation
          listingPO={listingDetailPO.listingPO}
          onLayout={({
            nativeEvent: {
              layout: {x, y, width, height},
            },
          }) => {
            this.postInfoY = y;
          }}
        />
      );
    }
  }

  renderKeyInformation() {
    return (
      <KeyInformation
        style={{backgroundColor: SRXColor.White}}
        listingDetailPO={this.state.listingDetailPO}
      />
    );
  }

  renderHomeValueEstimator() {
    const {listingDetailPO} = this.state;
    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const {listingPO} = listingDetailPO;
      if (!ObjectUtil.isEmpty(listingPO)) {
        const {type, postalCode, cdResearchSubType} = listingPO;
        const {listingId, refType} = this.props;

        const parameters = {
          postalCode,
          cdResearchSubtype: cdResearchSubType,
          isSale: type == 'S',
          listingId,
          listingType: refType,
        };

        return (
          <XTrend
            xTrendParameters={parameters}
            onTrendEndLoading={this.onTrendEndLoading}
            style={{backgroundColor: SRXColor.White}}
          />
        );
      }
    }
    return <View />;
  }

  renderMortgageCalculator() {
    const {listingDetailPO} = this.state;
    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const {listingPO} = listingDetailPO;
      return (
        <MortgageCalculator
          style={{backgroundColor: SRXColor.White}}
          listingPO={listingPO}
        />
      ); //
    }
  }

  renderRecommendedListings() {
    const {recommendedListings} = this.state;
    if (Array.isArray(recommendedListings)) {
      return (
        <ListingsCardPreview
          style={{backgroundColor: SRXColor.White}}
          data={recommendedListings}
          onListingSelected={selectedListing =>
            this.showListingDetails({listingPO: selectedListing})
          }
        />
      );
    } else {
      return <View />;
    }
  }

  renderContactAgent() {
    const {listingDetailPO} = this.state;
    var message = '';
    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const {listingPO} = listingDetailPO;
      if (!ObjectUtil.isEmpty(listingPO)) {
        message = listingPO.getEnquiryMessageTemplate();
      }
    }

    return (
      <AgentInformation
        style={{backgroundColor: SRXColor.White}}
        agentPO={this.state.agentPO}
        componentId={this.props.componentId}
        templateMessage={message}
      />
    );
  }

  renderLocationMap() {
    const {listingDetailPO} = this.state;
    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const {listingPO, amenitiesCategories} = listingDetailPO;
      if (!ObjectUtil.isEmpty(listingPO)) {
        const {cdResearchSubType} = listingPO;
        if (PropertyTypeUtil.isLanded(cdResearchSubType)) {
          const {fuzzyLatitude, fuzzyLongitude} = listingDetailPO;
          return (
            <Amenities
              style={{backgroundColor: SRXColor.White}}
              locationTitle={listingPO.getListingName()}
              latitude={fuzzyLatitude}
              longitude={fuzzyLongitude}
              showFuzzyLocation={true}
              componentId={this.props.componentId}
              amenitiesGroups={amenitiesCategories}
              source={AmenitiesSource.ListingDetails}
            />
          );
        } else {
          const {latitude, longitude} = listingDetailPO;
          return (
            <Amenities
              style={{backgroundColor: SRXColor.White}}
              locationTitle={listingPO.getListingName()}
              latitude={latitude}
              longitude={longitude}
              componentId={this.props.componentId}
              amenitiesGroups={amenitiesCategories}
              source={AmenitiesSource.ListingDetails}
            />
          );
        }
      }
    }
  }

  titleForSection = sectionContent => {
    if (sectionContent === SECTION_TYPE.Facilities) {
      return 'Facilities';
    } else if (sectionContent === SECTION_TYPE.Amenities) {
      return 'Amenities';
    } else if (sectionContent === SECTION_TYPE.LocationMap) {
      return 'Location';
    } else if (sectionContent === SECTION_TYPE.HomeValue) {
      const {listingDetailPO} = this.state;
      if (!ObjectUtil.isEmpty(listingDetailPO)) {
        const {listingPO} = listingDetailPO;
        if (!ObjectUtil.isEmpty(listingPO)) {
          const {type} = listingPO;
          if (type === 'R') {
            return 'Rental Estimator (X-Value)';
          }
        }
      }
      return 'Home Value Estimator (X-Value)';
    } else if (sectionContent === SECTION_TYPE.MortgageCal) {
      return 'Mortgage Calculator';
    } else if (sectionContent === SECTION_TYPE.RecommendedListings) {
      return 'Similar Listings';
    }
    return '';
  };

  renderAccordionHeader({content, index, isActive, sections}) {
    const sectionTitle = this.titleForSection(content);
    if (!ObjectUtil.isEmpty(sectionTitle)) {
      var headerIcon = null;
      if (
        content === SECTION_TYPE.HomeValue ||
        content === SECTION_TYPE.MortgageCal
      ) {
        headerIcon = (
          <FontAwesomeIcon
            name="tag"
            size={20}
            color={SRXColor.Teal}
            style={{
              marginRight: Spacing.XS,
              transform: [{rotate: '135deg'}],
            }}
          />
        );
      }
      return (
        <AccordianHeader
          isActive={isActive}
          headerStyle={{backgroundColor: SRXColor.White}}
          titleIcon={headerIcon}>
          {this.titleForSection(content)}
        </AccordianHeader>
      );
    } else {
      return <View />;
    }
  }

  renderContent({content, index, isActive, sections}) {
    if (content === SECTION_TYPE.Photo) {
      return this.renderListingImages();
    } else if (content === SECTION_TYPE.BasicInfo) {
      return this.renderBasicInformation(index);
    } else if (content === SECTION_TYPE.PostingInfo) {
      return this.renderPostingInformation(index);
    } else if (content === SECTION_TYPE.KeyInformation) {
      return this.renderKeyInformation(index);
    } else if (content === SECTION_TYPE.Description) {
      return this.renderDescription(index);
    } else if (content === SECTION_TYPE.Facilities) {
      return this.renderFacilities(index);
    } else if (content === SECTION_TYPE.MortgageCal) {
      return this.renderMortgageCalculator(index);
    } else if (content === SECTION_TYPE.RecommendedListings) {
      return this.renderRecommendedListings(index);
    } else if (content === SECTION_TYPE.ContactAgent) {
      return this.renderContactAgent(index);
    } else if (content === SECTION_TYPE.Amenities) {
      return <Amenities />;
    } else if (content === SECTION_TYPE.HomeValue) {
      return this.renderHomeValueEstimator(index);
    } else if (content === SECTION_TYPE.LocationMap) {
      return this.renderLocationMap(index);
    }

    return <View style={{height: 100, backgroundColor: SRXColor.White}} />;
  }

  renderFooter({content, index, isActive, sections}) {
    var canShowSection = index != sections.length - 1;
    if (canShowSection && content !== SECTION_TYPE.Photo) {
      return <Separator />;
    } else {
      return null;
    }
  }

  renderBottomView() {
    const {listingDetailPO} = this.state;
    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const {listingPO} = listingDetailPO;
      if (!ObjectUtil.isEmpty(listingPO)) {
        return (
          <View style={{backgroundColor: SRXColor.White}}>
            <Separator />
            <CallToActionBar
              componentId={this.props.componentId}
              listingPO={listingPO}
            />
          </View>
        );
      }
    }
  }

  // renderBottomView() {
  //   return (
  //     <SafeAreaView style={{backgroundColor: SRXColor.White}}>
  //       <Separator />
  //       {this.renderEnquiryAgentView()}
  //     </SafeAreaView>
  //   );
  // }

  renderEnquiryAgentView() {
    const {agentPO, listingDetailPO} = this.state;
    if (!ObjectUtil.isEmpty(agentPO)) {
      const agentPhotoURL = agentPO.getAgentPhoto();
      return (
        <TouchableWithoutFeedback onPress={this.viewAgentCV}>
          <View
            style={{
              padding: Spacing.M,
              flexDirection: 'row',
              alignItems: 'center',
            }}>
            <Image
              style={{height: 40, width: 40, borderRadius: 20}}
              defaultSource={Placeholder_Agent}
              source={{uri: agentPhotoURL}}
            />
            <Heading2
              style={{
                marginHorizontal: Spacing.XS,
                flex: 1,
              }}
              numberOfLines={2}>
              {agentPO.name}
            </Heading2>
            <Button
              buttonType={Button.buttonTypes.primary}
              onPress={this.enquiryClicked}>
              Enquire Now
            </Button>
          </View>
        </TouchableWithoutFeedback>
      );
    }
  }

  renderPostingInfoOnScroll() {
    const {listingDetailPO} = this.state;
    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const {listingPO} = listingDetailPO;
      if (!ObjectUtil.isEmpty(listingPO)) {
        const {agentPO} = listingPO;
        let agentName = '';
        if (!ObjectUtil.isEmpty(agentPO) && !ObjectUtil.isEmpty(agentPO.name)) {
          agentName = agentPO.name;
        }
        let postedDate = listingPO.getFormattedActualDatePosted(
          ' days ago',
        );
        return (
          <Animated.View
            style={{
              backgroundColor: SRXColor.White,
              opacity: this.state.fadeAnim,
              paddingHorizontal: Spacing.M,
              paddingTop: Spacing.XS,
              paddingBottom: Spacing.S,
              borderBottomLeftRadius: 10,
              borderBottomRightRadius: 10,
            }}>
            <BodyText>
              Posted
              {!ObjectUtil.isEmpty(postedDate) ? ' on ' + postedDate : ''}
              {!ObjectUtil.isEmpty(agentName) ? ' by ' : ''}
              {!ObjectUtil.isEmpty(agentName) ? (
                <Heading2 style={{color: SRXColor.Teal}}>
                  {agentName}
                </Heading2>
              ) : (
                ''
              )}
            </BodyText>
          </Animated.View>
        );
      }
    }
  }

  render() {
    const {
      listingDetailPO,
      activeSections,
      sections,
      loadingState,
    } = this.state;
    const {listingId} = this.props;
    const scrollViewMarginTop = IS_IPHONE_X
      ? -(TopBarConstants.statusBarHeight + TopBarConstants.topBarHeight)
      : 0;
    if (loadingState === LoadingState.Loading) {
      return (
        <SafeAreaView
          style={{flex: 1, justifyContent: 'center', alignItems: 'center'}}>
          <ActivityIndicator />
        </SafeAreaView>
      );
    } else if (loadingState === LoadingState.Loaded) {
      if (sections.length > 0 && Array.isArray(sections)) {
        return (
          <View
            style={{flex: 1, backgroundColor: 'white'}}
            forceInset={{bottom: 'never'}}>
            <KeyboardAwareScrollView
              style={{
                flex: 1,
                marginTop: scrollViewMarginTop,
              }}
              // keyboardShouldPersistTaps={"always"}
              onScroll={this.onScroll}>
              <Accordion
                activeSections={activeSections}
                sections={sections}
                expandMultiple={true}
                underlayColor={IS_IOS ? 'black' : '#D9D9D9'}
                renderHeader={(content, index, isActive, sections) =>
                  this.renderAccordionHeader({
                    content,
                    index,
                    isActive,
                    sections,
                  })
                }
                renderContent={(content, index, isActive, sections) =>
                  this.renderContent({
                    content,
                    index,
                    isActive,
                    sections,
                  })
                }
                renderFooter={(content, index, isActive, sections) =>
                  this.renderFooter({
                    content,
                    index,
                    isActive,
                    sections,
                  })
                }
                onChange={this.onActiveSectionsChanged}
              />
            </KeyboardAwareScrollView>
            <View
              style={{
                width: '100%',
                position: 'absolute',
              }}>
              <View
                style={{
                  backgroundColor: '#21212142',
                  height:
                    TopBarConstants.statusBarHeight +
                    TopBarConstants.topBarHeight,
                }}
              />
              {this.renderPostingInfoOnScroll()}
            </View>
            {this.renderBottomView()}
          </View>
        );
      } else {
        let urlString = '/listings/';
        let domain = DebugUtil.retrieveStoreDomainURL();
        urlString = domain + urlString + listingId;
        return (
          <View style={{flex: 1}}>
            <View
              style={{
                height:
                  TopBarConstants.statusBarHeight +
                  TopBarConstants.topBarHeight,
              }}
            />
            <WebView source={{uri: urlString}} />
          </View>
        );
      }
    } else {
      return <View />;
    }
  }
}

const mapStateToProps = state => {
  return {shortlistData: state.shortlistData, userPO: state.loginData.userPO};
};

export default connect(
  mapStateToProps,
  {shortlistListing, removeShortlist},
)(ListingDetails);
