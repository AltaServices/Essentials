package dev.alta.essentials.staff.collection

import dev.alta.essentials.utils.database.DatabaseUtils
import org.bson.Document

object StaffSettings {
    private val collection = DatabaseUtils.getCollection("staff_settings")
    private var staffGroups = mutableSetOf<String>()

    init {
        loadStaffGroups()
    }

    private fun loadStaffGroups() {
        val doc = collection.find(Document("_id", "staff_groups")).first()
        staffGroups = doc?.getList("groups", String::class.java)?.toMutableSet() ?: mutableSetOf()
    }

    fun isStaffGroup(group: String): Boolean {
        return staffGroups.contains(group)
    }

    fun addStaffGroup(group: String) {
        staffGroups.add(group)
        saveStaffGroups()
    }

    fun removeStaffGroup(group: String) {
        staffGroups.remove(group)
        saveStaffGroups()
    }

    private fun saveStaffGroups() {
        val doc = Document("_id", "staff_groups")
            .append("groups", ArrayList(staffGroups))
        
        collection.updateOne(
            Document("_id", "staff_groups"),
            Document("\$set", doc),
            com.mongodb.client.model.UpdateOptions().upsert(true)
        )
    }
} 