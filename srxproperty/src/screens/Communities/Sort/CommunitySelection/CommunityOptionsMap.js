import React, {Component} from 'react';
import {View, Dimensions, Text, Image} from 'react-native';
import PropTypes from 'prop-types';
import MapView, {
  Polygon,
  PROVIDER_GOOGLE,
  Marker,
  Circle,
  Callout,
} from 'react-native-maps';
import {connect} from 'react-redux';
import {
  Communities_MyPropertyIcon,
  Communities_MyPropertyIcon_iOS,
  Communities_Transparent_Image,
} from '../../../../assets';
import {SRXColor, IS_IOS} from '../../../../constants';
import {CommunityItem} from '../../../../dataObject';
import {ObjectUtil} from '../../../../utils';
import {
  FeatherIcon,
  SmallBodyText,
  ExtraSmallBodyText,
} from '../../../../components';
import {Spacing} from '../../../../styles';
import {CommunitiesConstant} from '../../Constants';

var {width} = Dimensions.get('window');

const INIT_REGION = {
  latitude: 1.372083,
  longitude: 103.819836,
  latitudeDelta: 0.18,
  longitudeDelta: 0.18,
};

class CommunityOptionsMap extends Component {
  constructor(props) {
    super(props);

    let estates = this.getEstatesFromCommunities();
    this.state = {
      estates,
    };
  }

  componentDidMount() {
    this.showCallout();
  }

  componentDidUpdate(prevProps) {
    if (prevProps.community != this.props.community) {
      const setting = this.getMapDisplaySettings(this.props.community);
      setTimeout(() => {
        this.map.animateToRegion(setting.region);
      }, 1);
    }
  }

  getEstatesFromCommunities() {
    const {communities} = this.props;
    var estates = [];
    if (!ObjectUtil.isEmpty(communities)) {
      communities.forEach(item => {
        let results = this.retrieveEstatesFromCommunity(item);
        estates = [...estates, ...results];
      });
    }
    return estates;
  }

  retrieveEstatesFromCommunity(communityItem) {
    if (
      communityItem.cdCommunityLevel ===
        CommunitiesConstant.communityLevel.estate ||
      communityItem.cdCommunityLevel ===
        CommunitiesConstant.communityLevel.postal ||
      communityItem.cdCommunityLevel ===
        CommunitiesConstant.communityLevel.private
    ) {
      return [communityItem];
    } else {
      var estates = [];
      if (!ObjectUtil.isEmpty(communityItem.childs)) {
        communityItem.childs.forEach(item => {
          let results = this.retrieveEstatesFromCommunity(item);
          estates = [...estates, ...results];
        });
      }
      return estates;
    }
  }

  getMapDisplaySettings(community) {
    var region = INIT_REGION;
    var markerCoord;
    if (
      !ObjectUtil.isEmpty(community) &&
      !ObjectUtil.isEmpty(community.geometry)
    ) {
      const geometry = community.geometry;
      if (geometry.type === 'Polygon') {
        const coordinates = geometry.coordinates;

        var minLats = coordinates[0][0][1];
        var maxLats = coordinates[0][0][1];
        var minLngs = coordinates[0][0][0];
        var maxLngs = coordinates[0][0][0];

        markerCoord = {
          latitude: minLats,
          longitude: minLngs,
        };

        coordinates.forEach(polygon => {
          polygon.forEach(element => {
            if (element[0] < minLngs) {
              minLngs = element[0];
            }
            if (element[0] > maxLngs) {
              maxLngs = element[0];
            }
            if (element[1] < minLats) {
              minLats = element[1];
            }
            if (element[1] > maxLats) {
              maxLats = element[1];

              markerCoord = {
                latitude: element[1],
                longitude: element[0],
              };
            }
          });
        });

        const latitude = ((minLats + maxLats) / 2 + maxLats) / 2;
        const longitude = (minLngs + maxLngs) / 2;

        const mapAspectRatio = width / 175;

        //Delta calculations is getting from https://stackoverflow.com/a/53868257
        //1.6 is a randomly picked number to shrink
        const latDelta = (maxLats - minLats) * 1.6;
        const lngDelta = latDelta * mapAspectRatio;
        region = {
          latitude: latitude,
          longitude: longitude,
          latitudeDelta: latDelta,
          longitudeDelta: lngDelta,
        };
      } else if (geometry.type === 'MultiPolygon') {
        const coordinates = geometry.coordinates;

        var minLats = coordinates[0][0][0][1];
        var maxLats = coordinates[0][0][0][1];
        var minLngs = coordinates[0][0][0][0];
        var maxLngs = coordinates[0][0][0][0];

        markerCoord = {
          latitude: minLats,
          longitude: minLngs,
        };

        coordinates.forEach(item => {
          item.forEach(polygon => {
            polygon.forEach(element => {
              if (element[0] < minLngs) {
                minLngs = element[0];
              }
              if (element[0] > maxLngs) {
                maxLngs = element[0];
              }
              if (element[1] < minLats) {
                minLats = element[1];
              }
              if (element[1] > maxLats) {
                maxLats = element[1];

                markerCoord = {
                  latitude: element[1],
                  longitude: element[0],
                };
              }
            });
          });
        });

        const latitude = ((minLats + maxLats) / 2 + maxLats) / 2;
        const longitude = (minLngs + maxLngs) / 2;

        const mapAspectRatio = width / 175;

        //Delta calculations is getting from https://stackoverflow.com/a/53868257
        //1.6 is a randomly picked number to shrink
        const latDelta = (maxLats - minLats) * 1.6;
        const lngDelta = latDelta * mapAspectRatio;
        region = {
          latitude: latitude,
          longitude: longitude,
          latitudeDelta: latDelta,
          longitudeDelta: lngDelta,
        };
      } else if (geometry.type === 'Point') {
        const mapAspectRatio = width / 175;

        const latDelta = 0.0016;
        const lngDelta = latDelta * mapAspectRatio;

        region = {
          latitude: geometry.latitude,
          longitude: geometry.longitude,
          latitudeDelta: latDelta,
          longitudeDelta: lngDelta,
        };

        markerCoord = {
          latitude: geometry.latitude,
          longitude: geometry.longitude,
        };
      }
    }

    return {
      region,
      markerCoord,
    };
  }

  togglePolygonMarker() {
    this.marker.showCallout();
  }

  showCallout() {
    setTimeout(() => {
      if (this.marker) {
        this.marker.showCallout();
      }
    }, 1);
  }

  renderEstates() {
    const {estates} = this.state;
    if (!ObjectUtil.isEmpty(estates) && Array.isArray(estates)) {
      return estates.map(element => {
        return this.renderEstateItem(element);
      });
    }
  }

  renderEstateItem(property) {
    if (
      !ObjectUtil.isEmpty(property) &&
      !ObjectUtil.isEmpty(property.geometry)
    ) {
      return (
        <Marker
          image={Communities_MyPropertyIcon_iOS}
          coordinate={{
            latitude: property.geometry.latitude,
            longitude: property.geometry.longitude,
          }}
        />
      );
    }
  }

  renderPolygonItem(coordinate) {
    const polygon = [];
    coordinate.map(item =>
      polygon.push({latitude: item[1], longitude: item[0]}),
    );
    return (
      <Polygon
        coordinates={polygon}
        strokeWidth={3}
        strokeColor={SRXColor.Teal}
        fillColor={SRXColor.Teal + '33'}
        tappable={true}
        onPress={() => this.togglePolygonMarker()}
      />
    );
  }

  renderPolygonGroups(polygons) {
    if (!ObjectUtil.isEmpty(polygons)) {
      return polygons.map(coordinates => this.renderPolygonItem(coordinates));
    }
  }

  renderPolygons() {
    const {community} = this.props;
    if (
      !ObjectUtil.isEmpty(community) &&
      !ObjectUtil.isEmpty(community.geometry) &&
      !ObjectUtil.isEmpty(community.geometry.coordinates)
    ) {
      if (community.geometry.type === 'Polygon') {
        return this.renderPolygonGroups(community.geometry.coordinates);
      } else if (community.geometry.type === 'MultiPolygon') {
        return community.geometry.coordinates.map(item => {
          return this.renderPolygonGroups(item);
        });
      }
    }
  }

  renderTransparentMarkerWithCallout(coordinate) {
    const {community} = this.props;
    if (!ObjectUtil.isEmpty(coordinate)) {
      return (
        <Marker
          image={Communities_Transparent_Image}
          ref={ref => (this.marker = ref)}
          coordinate={coordinate}>
          {this.renderCallout(community)}
        </Marker>
      );
    }
  }

  renderCallout(community) {
    return <Callout>{this.renderCalloutContent(community)}</Callout>;
  }

  renderCalloutContent(community) {
    if (IS_IOS) {
      return (
        <View
          style={{
            flexDirection: 'row',
            justifyContent: 'center',
            alignItems: 'center',
            flexWrap: 'wrap',
            flex: 1,
            maxWidth: 250,
            minWidth: 100,
          }}>
          <SmallBodyText>{community.name}</SmallBodyText>
          <View
            style={{
              flexDirection: 'row',
              marginLeft: Spacing.S,
              alignItems: 'center',
            }}>
            <FeatherIcon
              name={'users'}
              size={16}
              color={SRXColor.Teal}
              style={{marginRight: Spacing.XS / 2}}
            />
            <ExtraSmallBodyText style={{color: SRXColor.Gray}}>
              {community.membersTotal}
            </ExtraSmallBodyText>
          </View>
        </View>
      );
    } else {
      return (
        <View
          style={{
            justifyContent: 'center',
            alignItems: 'center',
            width: 130,
            height: 50,
          }}>
          <SmallBodyText style={{textAlign: 'center'}} numberOfLines={2}>
            {community.name}
          </SmallBodyText>
          <View
            style={{
              flexDirection: 'row',
              alignItems: 'center',
            }}>
            <FeatherIcon
              name={'users'}
              size={16}
              color={SRXColor.Teal}
              style={{marginRight: Spacing.XS / 2}}
            />
            <ExtraSmallBodyText style={{color: SRXColor.Gray}}>
              {community.membersTotal}
            </ExtraSmallBodyText>
          </View>
        </View>
      );
    }
  }

  renderSelectedCommunity() {
    const {community} = this.props;
    if (
      !ObjectUtil.isEmpty(community) &&
      !ObjectUtil.isEmpty(community.geometry) &&
      !ObjectUtil.isEmpty(community.geometry.type)
    ) {
      const geometry = community.geometry;
      if (geometry.type === 'Polygon' || geometry.type === 'MultiPolygon') {
        return this.renderPolygons();
      }
    }
  }

  render() {
    const {community} = this.props;
    var setting = this.getMapDisplaySettings(community);

    var region = setting.region; //will not be updated as this is used to display initial region
    var markerCoord = setting.markerCoord; //will be updated according to community

    return (
      <View style={{height: 175}}>
        <MapView
          provider={PROVIDER_GOOGLE}
          style={{flex: 1}}
          ref={r => {
            this.map = r;
          }}
          initialRegion={region}
          mapPadding={{
            top: 10,
            right: 10,
            bottom: 10,
            left: 10,
          }}
          paddingAdjustmentBehavior={'always'}
          zoomEnabled={false}
          zoomTapEnabled={false}
          zoomControlEnabled={false}
          rotateEnabled={false}
          scrollEnabled={false}
          pitchEnabled={false}
          onRegionChangeComplete={region => {
            this.showCallout();
          }}>
          {this.renderEstates()}
          {this.renderSelectedCommunity()}
          {this.renderTransparentMarkerWithCallout(markerCoord)}
        </MapView>
        {/*  Added a view to block touch at map*/}
        <View style={{height: 175, width: width, position: 'absolute'}} />
      </View>
    );
  }
}

CommunityOptionsMap.propTypes = {
  /**
   * CommunityItem to be displayed
   */
  community: PropTypes.oneOfType([
    PropTypes.instanceOf(CommunityItem),
    PropTypes.object,
  ]),

  /**
   * Communities allowing for select
   */
  communities: PropTypes.arrayOf(PropTypes.instanceOf(CommunityItem)),
};

export default CommunityOptionsMap;
