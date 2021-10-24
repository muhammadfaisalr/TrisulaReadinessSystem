package id.muhammadfaisal.trisula.utils

class Constant {
    class Role{
        companion object{
            const val SUPER_ADMIN = 2
            const val ADMIN = 1
            const val MEMBER = 0
        }
    }

    class RoleName{
        companion object{
            const val SUPER_ADMIN = "Super Admin"
            const val ADMIN = "Komandan"
            const val MEMBER = "Member"
        }
    }

    class GroupName{
        companion object{
            const val BAJRA_YUDHA_501 = "501 Bajra Yudha"
            const val UJWALA_YUDHA_502 = "502 Ujwala Yudha"
            const val MAYANGKARA_503 = "503 Mayangkara"
            const val BRIGIF_18 = "Brigif 18"
            const val DEPANDU_TAIKAM = "Depandu Taikam"
        }
    }

    class URL {
        companion object{
            const val FCM_GOOGLE = "https://fcm.googleapis.com/"
        }
    }

    class Key{
        companion object{
            const val USER_NAME = "USER_NAME"
            const val USER_EMAIL = "USER_EMAIL"
            const val USER_PASSWORD = "USER_PASSWORD"
            const val BUNDLING = "BUNDLING"
            const val ROLE_ID = "ROLE_ID"
            const val GROUP_ID = "GROUP_ID"
            const val GROUP_IMAGE = "GROUP_IMAGE"
            const val GROUP_NAME = "GROUP_NAME"
            const val INBOX_TITLE = "INBOX_TITLE"
            const val INBOX_CONTENT = "INBOX_CONTENT"
            const val INBOX_DATE = "INBOX_DATE"
            const val SUCCESS_TITLE = "SUCCESS_TITLE"
        }
    }
}