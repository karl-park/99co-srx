import { repository } from "./RealmDatabaseUtil";

function deleteAll() {
  repository.write(() => {
    repository.deleteAll();
  });
}

function deleteShortlistById(id) {
  shortllistIdString = id.toString();
  let searchShortlist = 'shortlistId = "' + shortllistIdString + '"';
  let deleteShortlist = repository
    .objects("ShortlistItems")
    .filtered(searchShortlist);
  repository.write(() => {
    repository.delete(deleteShortlist);
  });
}

function findAll(id) {
  shortllistIdString = id.toString();
  let searchShortlist = 'shortlistId = "' + shortllistIdString + '"';
  return repository.objects("ShortlistItems").filtered(searchShortlist);
}

function retrieveAll() {
  return repository.objects("ShortlistItems");
}

function save(id, customShortlistPO) {
  let shortlistPOString = JSON.stringify(customShortlistPO);
  repository.write(() => {
    try {
      repository.create("ShortlistItems", {
        shortlistId: id,
        shortlistitem: shortlistPOString
      });
    } catch (error) {
      console.log("Shortlist Database save error: " + error);
    }
  });
}

const ShortlistDatabaseUtil = {
  deleteAll,
  deleteShortlistById,
  findAll,
  retrieveAll,
  save
};

export { ShortlistDatabaseUtil };
