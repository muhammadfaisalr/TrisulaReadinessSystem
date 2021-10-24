package id.muhammadfaisal.trisula.model.firebase

data class GroupModelFirebase(
    var name : String? = "",
    var image : Int? = 0,
    var users : List<String>? = ArrayList(),
    var messages : List<InboxModelFirebase>? = ArrayList()
) {}