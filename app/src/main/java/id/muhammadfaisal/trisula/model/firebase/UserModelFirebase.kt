package id.muhammadfaisal.trisula.model.firebase

data class UserModelFirebase(
    var phoneNumber : Long? = 0,
    var name : String? = "",
    var email :String? = "",
    var password : String?  = "",
    var groupId : String? = "",
    var roleId : Int? = -1,
    var isActivated : Boolean = true
) {}