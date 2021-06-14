import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

const USERS_COLLECTION = "users";
const USER_INITIAL_DATA = {
  name: "",
  photoUrl: "",
  workplaceId: "",
  chosenRestaurant: "",
  chosenRestaurantName: "",
  conversations: [],
  likedRestaurants: [],
};

admin.initializeApp();

exports.initializeUserData = functions.auth.user().onCreate((user) =>{
  USER_INITIAL_DATA.name = user.displayName ?? "";
  USER_INITIAL_DATA.photoUrl = user.photoURL ?? "";
  admin.firestore().collection(USERS_COLLECTION)
      .doc(user.uid)
      .set(USER_INITIAL_DATA);
});

exports.onUserDeleted = functions.auth.user().onDelete((user) => {
  return admin.firestore().collection(USERS_COLLECTION).doc(user.uid).delete();
});

// const WORKPLACES_COLLECTION = "workplaces";
// const RESTAURANTS_COLLECTION = "restaurantsChosenByEmployees";

// exports.getUserById = functions.https.onCall(async (data, context) => {
//   if (context.auth == null) return;
//   return (await admin.auth().getUser(data.userId)).toJSON();
// });

// exports.getWorkplaceEmployees = functions.https.onCall((requestData) => {
//   return admin.firestore()
//       .collection(WORKPLACES_COLLECTION)
//       .doc(requestData.workplaceId)
//       .get().then((workplaceDocument) => {
//       const employeesIds: Array<string> = workplaceDocument.get("employees");
//         return getUsersWithAdditionalDataByIdlist(employeesIds);
//       });
// });

// exports.getEmployeesByChosenRestaurant = functions.https.onCall(
//     async (requestData, context) => {
//       if (context.auth == null) return;
//       const restaurantCollectionPath = WORKPLACES_COLLECTION +
//          "/" + requestData.workplaceId +
//          "/" + RESTAURANTS_COLLECTION;
//       const restaurantDocumemnt = await admin.firestore()
//           .collection(restaurantCollectionPath)
//           .doc(requestData.restaurantId)
//           .get();
//       if (!restaurantDocumemnt.exists) return JSON.stringify({});
//       const employeesIds: Array<string> = restaurantDocumemnt.get("clients");
//       return JSON.stringify(getUsersByIdList(employeesIds));
//     });


// const getUsersByIdList = async (usersIds : string[]) => {
//   const employeesIdentifiers = usersIds.map((userId) => ({uid: userId}));
//   return (await admin.auth().getUsers(employeesIdentifiers)).users;
// };

// const getUsersWithAdditionalDataByIdlist = (usersIds : string[]) => {
//   return getUsersByIdList(usersIds).then( async (users) => {
//     const usersWithAdditionalData : unknown[] = [];
//     for (let i = 0; i < users.length; i++) {
//       const userDoc = await admin.firestore()
//           .collection("users")
//           .doc(users[i].uid)
//           .get();
//       usersWithAdditionalData[i] = {
//         id: users[i].uid,
//         name: users[i].displayName,
//         photoUrl: users[i].photoURL,
//         restaurantId: userDoc.get("chosenRestaurant"),
//         restaurantName: userDoc.get("chosenRestaurantName"),
//       };
//     }
//     return JSON.stringify(usersWithAdditionalData);
//   });
// };
