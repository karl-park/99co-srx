import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {View, TouchableOpacity, ActionSheetIOS, Image} from 'react-native';
import {
  BodyText,
  ExtraSmallBodyText,
  FeatherIcon,
  MultiImagePicker,
  SmallBodyText,
  Subtext,
  TextInput,
} from '../../../../../../../components';
import ImagePicker from 'react-native-image-crop-picker';
import {Spacing} from '../../../../../../../styles';
import {IS_IOS, SRXColor} from '../../../../../../../constants';
import {CommonUtil, ObjectUtil} from '../../../../../../../utils';
import {Button} from '../../../../../../../components/Button/Button';

class CommunitiesGRCPostDetail extends Component {
  constructor(props) {
    super(props);

    this.TITLE_MAX = 70;
    this.DESC_MAX = 500;

    this.state = {
      details: props.details,
      detailErrors: props.detailErrors,
      photos: props.photos ?? [],
    };

    this.chooseImage = this.chooseImage.bind(this);
    this.showImagePicker = this.showImagePicker.bind(this);
    this.showCamera = this.showCamera.bind(this);
    this.onChangeTitle = this.onChangeTitle.bind(this);
    this.onChangeDescription = this.onChangeDescription.bind(this);
    this.onChangeExternalLink = this.onChangeExternalLink.bind(this);
    this.onUpdatePhotos = this.onUpdatePhotos.bind(this);
    this.removePhoto = this.removePhoto.bind(this);
  }

  componentDidUpdate(prevProps) {
    if (prevProps.details !== this.props.details) {
      this.setState({details: this.props.details});
    }

    if (prevProps.detailErrors !== this.props.detailErrors) {
      this.setState({detailErrors: this.props.detailErrors});
    }
  }

  chooseImage() {
    if (IS_IOS) {
      ActionSheetIOS.showActionSheetWithOptions(
        {
          message: 'Upload Image/Photo',
          options: ['cancel', 'Take Photo', 'Upload Photo'],
          cancelButtonIndex: 0,
        },
        buttonIndex => {
          if (buttonIndex === 1) {
            this.showCamera();
          } else if (buttonIndex === 2) {
            this.showImagePicker();
          }
        },
      );
    } else {
      MultiImagePicker.showMultiUploadOptions({
        onCameraSelect: this.showCamera,
        onUploadSelected: this.showImagePicker,
      });
    }
  }

  showImagePicker() {
    ImagePicker.openPicker({
      multiple: true,
      mediaType: 'photo',
      compressImageMaxWidth: 1024,
      compressImageMaxHeight: 1024,
    }).then(images => {
      if (IS_IOS) {
        let converted = images.map(item => {
          let photoItem = {
            uri: item.sourceURL,
            type: item.mime,
            name: item.fileName ?? '', //a random name for now, it is needed else fetch wont grab the data from the uri
          };
          return photoItem;
        });
        this.setState(
          {
            photos: [...this.state.photos, ...converted],
          },
          () => {
            this.onUpdatePhotos();
          },
        );
      } else {
        let converted = images.map(item => {
          var fileName = item.path.substring(item.path.lastIndexOf('/') + 1);
          let photoItem = {
            uri: item.path,
            type: item.mime,
            name: fileName ?? '',
          };
          return photoItem;
        });
        this.setState(
          {
            photos: [...this.state.photos, ...converted],
          },
          () => {
            this.onUpdatePhotos();
          },
        );
      }
    });
  }

  showCamera() {
    ImagePicker.openCamera({
      mediaType: 'photo',
      compressImageMaxWidth: 1024,
      compressImageMaxHeight: 1024,
    }).then(image => {
      console.log(image);
      if (IS_IOS) {
        let photoItem = {
          uri: image.sourceURL,
          type: image.mime,
          name: image.fileName ?? '', //a random name for now, it is needed else fetch wont grab the data from the uri
        };
        this.setState(
          {
            photos: [...this.state.photos, photoItem],
          },
          () => {
            this.onUpdatePhotos();
          },
        );
      } else {
        var fileName = image.path.substring(image.path.lastIndexOf('/') + 1);
        let photoItem = {
          uri: image.path,
          type: image.mime,
          name: fileName ?? '',
        };
        this.setState(
          {
            photos: [...this.state.photos, photoItem],
          },
          () => {
            this.onUpdatePhotos();
          },
        );
      }
    });
  }

  onUpdatePhotos() {
    const {photos} = this.state;
    const {onUpdatePhotos} = this.props;
    if (onUpdatePhotos) {
      onUpdatePhotos(photos);
    }
  }

  onChangeTitle(title) {
    const {onUpdateDetails} = this.props;
    const {details} = this.state;
    if (onUpdateDetails) {
      var detailTemp = {
        ...details,
        title,
      };
      onUpdateDetails(detailTemp);
    }
  }

  onChangeDescription(description) {
    const {onUpdateDetails} = this.props;
    const {details} = this.state;
    if (onUpdateDetails) {
      var detailTemp = {
        ...details,
        description,
      };
      onUpdateDetails(detailTemp);
    }
  }

  onChangeExternalLink(externalLink) {
    const {onUpdateDetails} = this.props;
    const {details} = this.state;
    if (onUpdateDetails) {
      var detailTemp = {
        ...details,
        externalLink,
      };
      onUpdateDetails(detailTemp);
    }
  }

  removePhoto(item, index) {
    const {photos} = this.state;
    if (index < photos.length) {
      console.log(item);
      photos.splice(index, 1);
      this.setState({photos});
    }
  }

  renderTitle() {
    const {title} = this.state.details;
    const {errorTitle} = this.state.detailErrors;
    return (
      <View>
        <BodyText style={{fontWeight: 'bold'}}>Title</BodyText>
        <TextInput
          style={{marginTop: Spacing.S}}
          defaultValue={title}
          editable={true}
          error={errorTitle}
          onChangeText={null}
          maxLength={this.TITLE_MAX}
          onChangeText={text => this.onChangeTitle(text)}
        />
        {errorTitle == '' ? (
          <View
            style={{
              alignItems: 'flex-end',
              justifyContent: 'flex-end',
            }}>
            <Subtext>
              {title.length} / {this.TITLE_MAX}
            </Subtext>
          </View>
        ) : null}
      </View>
    );
  }

  renderDescription() {
    const {description} = this.state.details;
    const {errorDescription} = this.state.detailErrors;
    return (
      <View style={{marginTop: Spacing.L}}>
        <BodyText style={{fontWeight: 'bold'}}>Description</BodyText>
        <TextInput
          style={{marginTop: Spacing.S}}
          defaultValue={description}
          multiline={true}
          error={errorDescription}
          editable={true}
          onChangeText={null}
          maxLength={this.DESC_MAX}
          onChangeText={description => this.onChangeDescription(description)}
        />
        {errorDescription == '' ? (
          <View style={{alignItems: 'flex-end', justifyContent: 'flex-end'}}>
            <Subtext>
              {description.length} / {this.DESC_MAX}
            </Subtext>
          </View>
        ) : null}
      </View>
    );
  }

  renderPictures() {
    const {photos} = this.state;
    return (
      <View style={{marginTop: Spacing.L}}>
        <BodyText style={{fontWeight: 'bold'}}>Picture</BodyText>
        <Subtext>Accpted Format: JPG, PNG, File size : 5MB</Subtext>
        <View
          style={{
            marginTop: Spacing.M,
            flexWrap: 'wrap',
            flexDirection: 'row',
          }}>
          {photos.map((item, index) => {
            return this.renderUploadedPhotos(item, index);
          })}
          {this.renderImagePicker()}
        </View>
      </View>
    );
  }

  renderImagePicker() {
    return (
      <TouchableOpacity
        style={{
          alignItems: 'center',
          justifyContent: 'center',
          width: 95,
          height: 80,
          borderColor: SRXColor.Teal,
          borderWidth: 1,
          borderRadius: 10,
          borderStyle: 'dashed',
          marginRight: Spacing.S,
          marginBottom: Spacing.S,
        }}
        onPress={() => this.chooseImage()}>
        <FeatherIcon name="camera" size={20} color={SRXColor.Gray} />
        <BodyText style={{color: SRXColor.LightGray}}>Upload</BodyText>
      </TouchableOpacity>
    );
  }

  renderUploadedPhotos(item, index) {
    if (!ObjectUtil.isEmpty(item)) {
      return (
        <View style={{marginRight: Spacing.S, marginBottom: Spacing.S}}>
          <Image
            style={{
              alignItems: 'center',
              justifyContent: 'center',
              width: 95,
              height: 80,
              borderColor: SRXColor.LightGray,
              borderWidth: 1,
              borderRadius: 10,
              marginRight: Spacing.S,
              marginBottom: Spacing.S,
            }}
            source={{uri: item.uri}}
          />
          <Button
            onPress={() => this.removePhoto(item, index)}
            buttonStyle={{
              backgroundColor: SRXColor.Teal,
              padding: 5,
              borderRadius: 13,
              borderColor: SRXColor.White,
              borderWidth: 1,
              position: 'absolute',
              right: 5,
              top: -7,
            }}
            leftView={
              <FeatherIcon name={'x'} size={11} color={SRXColor.White} />
            }
          />
        </View>
      );
    }
    return <View />;
  }

  renderExternalLink() {
    const {externalLink} = this.props.details;
    return (
      <View style={{marginTop: Spacing.L}}>
        <BodyText style={{fontWeight: 'bold'}}>External link</BodyText>
        <TextInput
          style={{marginTop: Spacing.S}}
          defaultValue={externalLink}
          error={''}
          editable={true}
          onChangeText={link => this.onChangeExternalLink(link)}
        />
      </View>
    );
  }

  render() {
    const {details} = this.state;
    if (!ObjectUtil.isEmpty(details)) {
      return (
        <View
          style={{
            padding: Spacing.L,
            borderRadius: 10,
            backgroundColor: SRXColor.White,
          }}>
          {this.renderTitle()}
          {this.renderDescription()}
          {this.renderPictures()}
          {this.renderExternalLink()}
        </View>
      );
    }
    return <View />;
  }
}

CommunitiesGRCPostDetail.propTypes = {
  details: PropTypes.object.isRequired,

  detailErrors: PropTypes.object.isRequired,

  photos: PropTypes.arrayOf(PropTypes.object),

  onUpdateDetails: PropTypes.func.isRequired,

  onUpdatePhotos: PropTypes.func.isRequired,
};

export {CommunitiesGRCPostDetail};
