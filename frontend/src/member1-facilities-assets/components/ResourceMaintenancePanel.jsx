import React, { useState, useEffect } from 'react';
import { 
  Wrench, Calendar, Clock, DollarSign, User, AlertCircle, CheckCircle, 
  XCircle, Plus, Filter, Search, Edit, Trash2, Eye 
} from 'lucide-react';

const ResourceMaintenancePanel = () => {
  const [maintenanceRecords, setMaintenanceRecords] = useState([]);
  const [filteredRecords, setFilteredRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [typeFilter, setTypeFilter] = useState('all');
  const [showModal, setShowModal] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState(null);
  const [viewMode, setViewMode] = useState('list'); // list, calendar, kanban

  useEffect(() => {
    fetchMaintenanceRecords();
  }, []);

  useEffect(() => {
    filterRecords();
  }, [maintenanceRecords, searchTerm, statusFilter, typeFilter]);

  const fetchMaintenanceRecords = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/member1/maintenance');
      const data = await response.json();
      setMaintenanceRecords(data);
    } catch (error) {
      console.error('Failed to fetch maintenance records:', error);
    } finally {
      setLoading(false);
    }
  };

  const filterRecords = () => {
    let filtered = maintenanceRecords;

    if (searchTerm) {
      filtered = filtered.filter(record =>
        record.resource?.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        record.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        record.technicianName?.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    if (statusFilter !== 'all') {
      filtered = filtered.filter(record => record.status === statusFilter);
    }

    if (typeFilter !== 'all') {
      filtered = filtered.filter(record => record.maintenanceType === typeFilter);
    }

    setFilteredRecords(filtered);
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'SCHEDULED': return <Calendar className="w-4 h-4 text-blue-500" />;
      case 'IN_PROGRESS': return <Wrench className="w-4 h-4 text-orange-500" />;
      case 'COMPLETED': return <CheckCircle className="w-4 h-4 text-green-500" />;
      case 'CANCELLED': return <XCircle className="w-4 h-4 text-gray-500" />;
      case 'OVERDUE': return <AlertCircle className="w-4 h-4 text-red-500" />;
      default: return <Clock className="w-4 h-4 text-gray-400" />;
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'SCHEDULED': return 'bg-blue-100 text-blue-800';
      case 'IN_PROGRESS': return 'bg-orange-100 text-orange-800';
      case 'COMPLETED': return 'bg-green-100 text-green-800';
      case 'CANCELLED': return 'bg-gray-100 text-gray-800';
      case 'OVERDUE': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getTypeIcon = (type) => {
    switch (type) {
      case 'ROUTINE': return <Clock className="w-4 h-4 text-gray-500" />;
      case 'REPAIR': return <Wrench className="w-4 h-4 text-red-500" />;
      case 'INSPECTION': return <Eye className="w-4 h-4 text-blue-500" />;
      case 'UPGRADE': return <Plus className="w-4 h-4 text-green-500" />;
      case 'EMERGENCY': return <AlertCircle className="w-4 h-4 text-red-500" />;
      default: return <Wrench className="w-4 h-4 text-gray-400" />;
    }
  };

  const MaintenanceRecordCard = ({ record }) => (
    <div className="bg-white rounded-lg shadow p-6 hover:shadow-md transition-shadow">
      <div className="flex justify-between items-start mb-4">
        <div className="flex-1">
          <h3 className="text-lg font-semibold text-gray-900">{record.resource?.name}</h3>
          <p className="text-sm text-gray-600 mt-1">{record.description}</p>
        </div>
        <div className="flex items-center space-x-2">
          {getStatusIcon(record.status)}
          <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(record.status)}`}>
            {record.status.replace('_', ' ')}
          </span>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4 mb-4">
        <div className="flex items-center text-sm text-gray-600">
          {getTypeIcon(record.maintenanceType)}
          <span className="ml-2">{record.maintenanceType}</span>
        </div>
        <div className="flex items-center text-sm text-gray-600">
          <Calendar className="w-4 h-4 mr-2" />
          <span>{new Date(record.scheduledDate).toLocaleDateString()}</span>
        </div>
        {record.technicianName && (
          <div className="flex items-center text-sm text-gray-600">
            <User className="w-4 h-4 mr-2" />
            <span>{record.technicianName}</span>
          </div>
        )}
        {record.cost && (
          <div className="flex items-center text-sm text-gray-600">
            <DollarSign className="w-4 h-4 mr-2" />
            <span>${record.cost}</span>
          </div>
        )}
      </div>

      {record.completedDate && (
        <div className="mb-4">
          <p className="text-sm text-gray-600">
            <span className="font-medium">Completed:</span>{' '}
            {new Date(record.completedDate).toLocaleDateString()}
          </p>
        </div>
      )}

      {record.notes && (
        <div className="mb-4">
          <p className="text-sm text-gray-600">
            <span className="font-medium">Notes:</span> {record.notes}
          </p>
        </div>
      )}

      <div className="flex space-x-2">
        {record.status === 'SCHEDULED' && (
          <button className="flex-1 bg-blue-600 text-white px-3 py-2 rounded-md hover:bg-blue-700 transition-colors text-sm">
            Start Work
          </button>
        )}
        {record.status === 'IN_PROGRESS' && (
          <button className="flex-1 bg-green-600 text-white px-3 py-2 rounded-md hover:bg-green-700 transition-colors text-sm">
            Complete
          </button>
        )}
        <button className="px-3 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors text-sm">
          Edit
        </button>
        <button className="px-3 py-2 text-red-600 border border-red-300 rounded-md hover:bg-red-50 transition-colors text-sm">
          Cancel
        </button>
      </div>
    </div>
  );

  const KanbanBoard = () => {
    const columns = [
      { id: 'SCHEDULED', title: 'Scheduled', color: 'blue' },
      { id: 'IN_PROGRESS', title: 'In Progress', color: 'orange' },
      { id: 'COMPLETED', title: 'Completed', color: 'green' },
      { id: 'CANCELLED', title: 'Cancelled', color: 'gray' },
    ];

    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {columns.map(column => (
          <div key={column.id} className="bg-gray-50 rounded-lg p-4">
            <h3 className="font-semibold text-gray-900 mb-4 flex items-center">
              {getStatusIcon(column.id)}
              <span className="ml-2">{column.title}</span>
              <span className="ml-auto bg-gray-200 text-gray-700 px-2 py-1 rounded-full text-xs">
                {filteredRecords.filter(r => r.status === column.id).length}
              </span>
            </h3>
            <div className="space-y-3">
              {filteredRecords
                .filter(record => record.status === column.id)
                .map(record => (
                  <div key={record.id} className="bg-white rounded-lg p-4 shadow-sm">
                    <h4 className="font-medium text-gray-900 text-sm">{record.resource?.name}</h4>
                    <p className="text-xs text-gray-600 mt-1 line-clamp-2">{record.description}</p>
                    <div className="flex items-center mt-2 text-xs text-gray-500">
                      <Calendar className="w-3 h-3 mr-1" />
                      {new Date(record.scheduledDate).toLocaleDateString()}
                    </div>
                  </div>
                ))}
            </div>
          </div>
        ))}
      </div>
    );
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-900">Resource Maintenance</h1>
        <div className="flex space-x-3">
          <button
            onClick={() => setShowModal(true)}
            className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
          >
            <Plus className="w-4 h-4 mr-2" />
            Schedule Maintenance
          </button>
          <button className="flex items-center px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors">
            <Filter className="w-4 h-4 mr-2" />
            Filter
          </button>
        </div>
      </div>

      {/* View Toggle */}
      <div className="flex space-x-2">
        {['list', 'kanban', 'calendar'].map(view => (
          <button
            key={view}
            onClick={() => setViewMode(view)}
            className={`px-4 py-2 rounded-md transition-colors ${
              viewMode === view
                ? 'bg-blue-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            {view.charAt(0).toUpperCase() + view.slice(1)}
          </button>
        ))}
      </div>

      {/* Search and Filters */}
      <div className="flex flex-col md:flex-row gap-4">
        <div className="flex-1">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
            <input
              type="text"
              placeholder="Search maintenance records..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="all">All Statuses</option>
          <option value="SCHEDULED">Scheduled</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="COMPLETED">Completed</option>
          <option value="CANCELLED">Cancelled</option>
          <option value="OVERDUE">Overdue</option>
        </select>
        <select
          value={typeFilter}
          onChange={(e) => setTypeFilter(e.target.value)}
          className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="all">All Types</option>
          <option value="ROUTINE">Routine</option>
          <option value="REPAIR">Repair</option>
          <option value="INSPECTION">Inspection</option>
          <option value="UPGRADE">Upgrade</option>
          <option value="EMERGENCY">Emergency</option>
        </select>
      </div>

      {/* Content */}
      {viewMode === 'list' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredRecords.map(record => (
            <MaintenanceRecordCard key={record.id} record={record} />
          ))}
        </div>
      )}

      {viewMode === 'kanban' && <KanbanBoard />}

      {viewMode === 'calendar' && (
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold mb-4">Calendar View</h3>
          <div className="text-center text-gray-500 py-8">
            Calendar view coming soon...
          </div>
        </div>
      )}

      {/* Add Maintenance Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-2xl">
            <h2 className="text-xl font-bold mb-4">Schedule Maintenance</h2>
            <form className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Resource</label>
                  <select className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
                    <option>Select Resource</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Maintenance Type</label>
                  <select className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
                    <option value="ROUTINE">Routine</option>
                    <option value="REPAIR">Repair</option>
                    <option value="INSPECTION">Inspection</option>
                    <option value="UPGRADE">Upgrade</option>
                    <option value="EMERGENCY">Emergency</option>
                  </select>
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
                <textarea className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" rows="3" />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Scheduled Date</label>
                  <input type="datetime-local" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Estimated Cost</label>
                  <input type="number" step="0.01" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
              </div>
              <div className="flex space-x-3">
                <button type="button" className="flex-1 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors">
                  Schedule Maintenance
                </button>
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="flex-1 bg-gray-100 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-200 transition-colors"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default ResourceMaintenancePanel;
