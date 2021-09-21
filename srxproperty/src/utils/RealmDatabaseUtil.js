import Realm from "realm";

const repository = new Realm({
  schema: [
    {
      name: "ConversationList",
      properties: { conversationId: "string" }
    },
    {
      name: "ChatMessages",
      primaryKey: "messageId",
      properties: {
        conversationId: "string",
        messageId: "int",
        messages: "string"
      }
    },
    {
      name: "ShortlistItems", //2nd ways ShortlistItems  // 1st way  ShortlistListings
      properties: { shortlistId: "int", shortlistitem: "string" } //2nd ways shortlistitem : string // 1st way shortlistItems
    }
  ],
  deleteRealmIfMigrationNeeded: true
  /** Remove deleteRealmIfMigrationNeeded if needed to do migration
   *  Note that if you wish to migrate, you need to add schema version (default version is omitted)
   *  and refer to: https://stackoverflow.com/questions/42868522/react-native-realm-migration for example
   */
});

export { repository };
