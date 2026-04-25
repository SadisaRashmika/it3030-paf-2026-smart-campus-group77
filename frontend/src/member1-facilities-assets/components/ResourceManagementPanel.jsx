import React, { useState, useEffect } from "react";
import { Package, Plus, Search, Table as TableIcon, CheckCircle2, XCircle, X, Edit2, Trash2, AlertCircle, Trash } from "lucide-react";
import { requestJson } from "../../services/apiClient";

export default function ResourceManagementPanel() {
  const [resources, setResources] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  
  // Modals States
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  // Data States
  const [editingResource, setEditingResource] = useState(null);
  const [resourceToDelete, setResourceToDelete] = useState(null);
  const [formData, setFormData] = useState({ name: "", type: "LAB", available: true });
  const [formError, setFormError] = useState("");
  const [deleteError, setDeleteError] = useState("");

  useEffect(() => {
    fetchResources();
  }, []);

  const fetchResources = async () => {
    try {
      setLoading(true);
      const data = await requestJson("/api/member2/resources");
      setResources(data || []);
    } catch (error) {
      console.error("Failed to fetch resources:", error);
    } finally {
      setLoading(false);
    }
  };

  const openAddModal = () => {
    setEditingResource(null);
    setFormData({ name: "", type: "LAB", available: true });
    setFormError("");
    setIsModalOpen(true);
  };

  const openEditModal = (resource) => {
    setEditingResource(resource);
    setFormData({ 
      name: resource.name, 
      type: resource.type, 
      available: resource.available 
    });
    setFormError("");
    setIsModalOpen(true);
  };

  const openDeleteModal = (resource) => {
    console.log("Opening delete modal for:", resource);
    setResourceToDelete(resource);
    setDeleteError("");
    setIsDeleteModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.name.trim()) {
      setFormError("Resource name is required");
      return;
    }

    try {
      setIsSubmitting(true);
      const url = editingResource 
        ? `/api/member2/resources/${editingResource.id}` 
        : "/api/member2/resources";
      
      const method = editingResource ? "PUT" : "POST";

      await requestJson(url, {
        method,
        body: JSON.stringify(formData)
      });

      setIsModalOpen(false);
      fetchResources();
    } catch (error) {
      setFormError(error.message || "Something went wrong. Please try again.");
    } finally {
      setIsSubmitting(false);
    }
  };

  const confirmDelete = async () => {
    if (!resourceToDelete) {
      console.error("No resource selected to delete");
      return;
    }

    console.log(`Attempting to delete resource: ${resourceToDelete.name} (ID: ${resourceToDelete.id})`);
    
    try {
      setIsSubmitting(true);
      setDeleteError("");
      
      // Explicitly ensuring no body is sent with DELETE which sometimes confuses proxies
      await requestJson(`/api/member2/resources/${resourceToDelete.id}`, { 
        method: "DELETE" 
      });

      console.log("Delete successful");
      setIsDeleteModalOpen(false);
      setResourceToDelete(null);
      fetchResources();
    } catch (error) {
      console.error("Delete failed:", error);
      // Detailed error message for the user
      const msg = error.message?.toLowerCase().includes("constraint") 
        ? "Cannot delete this resource because it is currently linked to existing bookings."
        : error.message || "Server error occurred during deletion.";
      setDeleteError(msg);
    } finally {
      setIsSubmitting(false);
    }
  };

  const filteredResources = resources.filter(res => 
    res.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    res.type.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="space-y-6">
      {/* Header Section */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h2 className="font-display text-2xl font-extrabold text-slate-900">Resource Inventory</h2>
          <p className="text-sm text-slate-500">Manage campus labs, classrooms, and shared facilities.</p>
        </div>
        <button 
          onClick={openAddModal}
          className="inline-flex items-center gap-2 rounded-xl bg-amber-500 px-4 py-2.5 text-sm font-bold text-amber-950 shadow-lg shadow-amber-200 transition hover:bg-amber-600 hover:shadow-amber-300"
        >
          <Plus size={18} /> Add New Resource
        </button>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <p className="text-xs font-bold uppercase tracking-wider text-slate-500">Total Resources</p>
          <p className="mt-2 text-3xl font-black text-slate-900">{resources.length}</p>
        </div>
        <div className="rounded-2xl border border-slate-200 bg-emerald-50 p-5 shadow-sm">
          <p className="text-xs font-bold uppercase tracking-wider text-emerald-600">Available Now</p>
          <p className="mt-2 text-3xl font-black text-emerald-700">
            {resources.filter(r => r.available).length}
          </p>
        </div>
        <div className="rounded-2xl border border-slate-200 bg-amber-50 p-5 shadow-sm">
          <p className="text-xs font-bold uppercase tracking-wider text-amber-700">Resource Types</p>
          <p className="mt-2 text-3xl font-black text-amber-800">
            {new Set(resources.map(r => r.type)).size}
          </p>
        </div>
      </div>

      {/* Table Section */}
      <div className="rounded-2xl border border-slate-200 bg-white shadow-sm overflow-hidden">
        <div className="border-b border-slate-100 bg-slate-50/50 p-4">
          <div className="relative max-w-md">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
            <input
              type="text"
              placeholder="Search resources by name or type..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full rounded-xl border border-slate-200 bg-white py-2 pl-10 pr-4 text-sm transition focus:border-amber-400 focus:outline-none focus:ring-4 focus:ring-amber-100"
            />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead>
              <tr className="bg-slate-50 text-xs font-bold uppercase tracking-wider text-slate-500">
                <th className="px-6 py-4">Resource Details</th>
                <th className="px-6 py-4">Type</th>
                <th className="px-6 py-4">Status</th>
                <th className="px-6 py-4">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan="4" className="px-6 py-12 text-center text-slate-500">
                    <div className="flex flex-col items-center gap-3">
                      <div className="h-8 w-8 animate-spin rounded-full border-4 border-blue-100 border-t-blue-600"></div>
                      <p className="font-medium">Loading inventory...</p>
                    </div>
                  </td>
                </tr>
              ) : filteredResources.length === 0 ? (
                <tr>
                  <td colSpan="4" className="px-6 py-12 text-center text-slate-500">
                    <div className="flex flex-col items-center gap-3 text-slate-400">
                      <Package size={48} strokeWidth={1.5} />
                      <p className="font-medium">No resources found matching your search.</p>
                    </div>
                  </td>
                </tr>
              ) : (
                filteredResources.map((res) => (
                  <tr key={res.id} className="group transition hover:bg-slate-50/80">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-slate-100 text-slate-500 group-hover:bg-blue-100 group-hover:text-blue-600 transition">
                          <TableIcon size={20} />
                        </div>
                        <div>
                          <p className="font-bold text-slate-900">{res.name}</p>
                          <p className="text-[11px] text-slate-500">Resource ID: #{res.id}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <span className="inline-flex items-center rounded-lg bg-slate-100 px-2.5 py-1 text-xs font-bold text-slate-600">
                        {res.type}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      {res.available ? (
                        <div className="flex items-center gap-2 font-bold text-emerald-600">
                          <CheckCircle2 size={16} /> Active
                        </div>
                      ) : (
                        <div className="flex items-center gap-2 font-bold text-rose-500">
                          <XCircle size={16} /> Maintenance
                        </div>
                      )}
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        <button 
                          onClick={() => openEditModal(res)}
                          className="flex h-8 w-8 items-center justify-center rounded-lg bg-slate-100 text-slate-600 hover:bg-amber-100 hover:text-amber-700 transition"
                          title="Edit"
                        >
                          <Edit2 size={14} />
                        </button>
                        <button 
                          onClick={() => openDeleteModal(res)}
                          className="flex h-8 w-8 items-center justify-center rounded-lg bg-slate-100 text-slate-600 hover:bg-rose-100 hover:text-rose-700 transition"
                          title="Delete"
                        >
                          <Trash2 size={14} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal - Add/Edit Resource */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 p-4 backdrop-blur-sm">
          <div className="w-full max-w-md animate-in slide-in-from-bottom-4 duration-300 rounded-2xl border border-slate-200 bg-white p-6 shadow-2xl">
            <div className="mb-6 flex items-center justify-between">
              <h3 className="text-xl font-bold text-slate-900">
                {editingResource ? "Edit Resource" : "Add New Resource"}
              </h3>
              <button 
                onClick={() => setIsModalOpen(false)}
                className="rounded-lg p-1 text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition"
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4">
              {formError && (
                <div className="flex items-center gap-2 rounded-xl bg-rose-50 p-3 text-sm font-medium text-rose-600 border border-rose-100">
                  <AlertCircle size={16} /> {formError}
                </div>
              )}

              <div className="space-y-2">
                <label className="text-sm font-bold text-slate-700">Resource Name</label>
                <input
                  type="text"
                  placeholder="e.g. Lab 01 - Computer Science"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className="w-full rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-sm transition focus:border-amber-400 focus:outline-none focus:ring-4 focus:ring-amber-100"
                />
              </div>

              <div className="space-y-2">
                <label className="text-sm font-bold text-slate-700">Resource Type</label>
                <select
                  value={formData.type}
                  onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                  className="w-full rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-sm transition focus:border-amber-400 focus:outline-none focus:ring-4 focus:ring-amber-100"
                >
                  <option value="LAB">Laboratory</option>
                  <option value="CLASSROOM">Classroom</option>
                  <option value="HALL">Seminar Hall</option>
                  <option value="DISCUSSION_ROOM">Discussion Room</option>
                  <option value="EQUIPMENT">Special Equipment</option>
                </select>
              </div>

              <div className="flex items-center justify-between pt-2">
                <div className="flex items-center gap-2">
                  <input
                    type="checkbox"
                    id="available"
                    checked={formData.available}
                    onChange={(e) => setFormData({ ...formData, available: e.target.checked })}
                    className="h-4 w-4 rounded border-slate-300 text-amber-600 focus:ring-amber-500"
                  />
                  <label htmlFor="available" className="text-sm font-bold text-slate-700">Active and bookable</label>
                </div>
              </div>

              <div className="mt-8 flex items-center gap-3">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="flex-1 rounded-xl border border-slate-200 bg-white py-2.5 text-sm font-bold text-slate-600 transition hover:bg-slate-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="flex-[2] inline-flex items-center justify-center gap-2 rounded-xl bg-amber-500 py-2.5 text-sm font-bold text-amber-950 shadow-lg shadow-amber-200 transition hover:bg-amber-600 disabled:opacity-70 disabled:cursor-not-allowed"
                >
                  {isSubmitting ? "Saving..." : editingResource ? "Save Changes" : "Create Resource"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal - Delete Confirmation */}
      {isDeleteModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 p-4 backdrop-blur-sm">
          <div className="w-full max-w-sm animate-in zoom-in-95 duration-200 rounded-2xl border border-slate-200 bg-white p-6 shadow-2xl">
            <div className="flex flex-col items-center text-center">
              <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-full bg-rose-50 text-rose-600">
                <Trash size={28} />
              </div>
              <h3 className="text-xl font-bold text-slate-900">Delete Resource?</h3>
              <p className="mt-2 text-sm text-slate-600">
                Are you sure you want to delete <span className="font-bold text-slate-900">"{resourceToDelete?.name}"</span>? 
                This action is permanent and cannot be undone.
              </p>
            </div>

            {deleteError && (
              <div className="mt-4 flex items-start gap-2 rounded-xl bg-amber-50 p-3 text-xs font-medium text-amber-700 border border-amber-200">
                <AlertCircle size={14} className="mt-0.5 shrink-0" /> {deleteError}
              </div>
            )}

            <div className="mt-8 flex items-center gap-3">
              <button
                type="button"
                onClick={() => setIsDeleteModalOpen(false)}
                className="flex-1 rounded-xl border border-slate-200 bg-white py-2.5 text-sm font-bold text-slate-600 transition hover:bg-slate-50"
              >
                No, Keep it
              </button>
              <button
                type="button"
                disabled={isSubmitting}
                onClick={confirmDelete}
                className="flex-1 inline-flex items-center justify-center gap-2 rounded-xl bg-rose-600 py-2.5 text-sm font-bold text-white shadow-lg shadow-rose-200 transition hover:bg-rose-700 disabled:opacity-70"
              >
                {isSubmitting ? "Deleting..." : "Yes, Delete"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
