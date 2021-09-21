import {CommonUtil, ObjectUtil} from '../utils';

class ChatMessagePO {
  constructor(data) {
    if (data) {
      if (data.message) {
        this._id = data.messageId;
        this.messageId = data.messageId;
      }
      //this is a temporary id for gifted chat api, it will be override after the message is sent to server
      let randomId = Math.round(Math.random() * 1000000);
      this._id = randomId;
      this.messageId = randomId;

      this.conversationId = data.conversationId;
      this.text = data.message === 'Uploaded an image!' ? null : data.message;
      this.createdAt = data.dateSent;
      if (!ObjectUtil.isEmpty(data.thumbUrl)) {
        this.image = data.thumbUrl;
      }

      if (data.isFromOtherUser == 'false') {
        this.user = {
          _id: data.sourceUserEncryptedId,
          name: data.sourceUserName,
        };
      } else {
        this.user = {
          _id: data.otherUser,
          name: data.otherUserName,
        };
      }

      if (data.listingId) {
        this.listingId = data.listingId;
      }
      if (data.listing) {
        this.listing = data.listing;
      }
    }
  }
}

export {ChatMessagePO};
