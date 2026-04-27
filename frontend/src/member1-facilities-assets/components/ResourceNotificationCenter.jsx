import React, { useState, useEffect } from 'react';
import { 
  Bell, BellRing, CheckCircle, XCircle, AlertTriangle, Clock, Calendar,
  Users, CreditCard, Settings, Search, Filter, RefreshCw, Trash2, Eye,
  Archive, ChevronDown, User, MessageSquare, DollarSign, Wrench, Zap,
  Info, Check, X, Plus, Send, Mail, Smartphone, Globe
} from 'lucide-react';

const ResourceNotificationCenter = () => {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [selectedNotification, setSelectedNotification] = useState(null);
  const [viewMode, setViewMode] = useState('all'); // all, unread, archived
  const [filterType, setFilterType] = useState('all');
  const [filterPriority, setFilterPriority] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');
  const [showComposeModal, setShowComposeModal] = useState(false);
  const [showDetailsModal, setShowDetailsModal] = useState(false);

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/member1/notifications/user/CURRENT_USER');
      const data = await response.json();
      setNotifications(data);
      setUnreadCount(data.filter(n => n.status === 'UNREAD').length);
    } catch (error) {
      console.error('Failed to fetch notifications:', error);
    } finally {
      setLoading(false);
    }
  };

  const getTypeIcon = (type) => {
    switch (type) {
      case 'BOOKING_CONFIRMED': return <CheckCircle className="w-4 h-4 text-green-500" />;
      case 'BOOKING_REMINDER': return <Clock className="w-4 h-4 text-blue-500" />;
      case 'BOOKING_CANCELLED': return <XCircle className="w-4 h-4 text-red-500" />;
      case 'BOOKING_APPROVED': return <CheckCircle className="w-4 h-4 text-green-500" />;
      case 'BOOKING_REJECTED': return <XCircle className="w-4 h-4 text-red-500" />;
      case 'MAINTENANCE_SCHEDULED': return <Wrench className="w-4 h-4 text-orange-500" />;
      case 'MAINTENANCE_COMPLETED': return <CheckCircle className="w-4 h-4 text-green-500" />;
      case 'MAINTENANCE_OVERDUE': return <AlertTriangle className="w-4 h-4 text-red-500" />;
      case 'RESOURCE_AVAILABLE': return <CheckCircle className="w-4 h-4 text-green-500" />;
      case 'RESOURCE_UNAVAILABLE': return <XCircle className="w-4 h-4 text-red-500" />;
      case 'RESOURCE_LOW_STOCK': return <AlertTriangle className="w-4 h-4 text-yellow-500" />;
      case 'PAYMENT_DUE': return <CreditCard className="w-4 h-4 text-orange-500" />;
      case 'PAYMENT_OVERDUE': return <AlertTriangle className="w-4 h-4 text-red-500" />;
      case 'PAYMENT_CONFIRMED': return <CheckCircle className="w-4 h-4 text-green-500" />;
      case 'REFUND_PROCESSED': return <DollarSign className="w-4 h-4 text-blue-500" />;
      case 'RATING_REQUEST': return <MessageSquare className="w-4 h-4 text-purple-500" />;
      case 'SYSTEM_ALERT': return <AlertTriangle className="w-4 h-4 text-red-500" />;
      case 'ANNOUNCEMENT': return <Info className="w-4 h-4 text-blue-500" />;
      case 'SECURITY_ALERT': return <AlertTriangle className="w-4 h-4 text-red-500" />;
      case 'EQUIPMENT_ISSUE': return <Wrench className="w-4 h-4 text-orange-500" />;
      case 'ACCESS_GRANTED': return <CheckCircle className="w-4 h-4 text-green-500" />;
      case 'ACCESS_DENIED': return <XCircle className="w-4 h-4 text-red-500" />;
      case 'SCHEDULE_CHANGE': return <Calendar className="w-4 h-4 text-yellow-500" />;
      default: return <Bell className="w-4 h-4 text-gray-500" />;
    }
  };

  const getPriorityColor = (priority) => {
    switch (priority) {
      case 'LOW': return 'bg-gray-100 text-gray-800';
      case 'NORMAL': return 'bg-blue-100 text-blue-800';
      case 'HIGH': return 'bg-orange-100 text-orange-800';
      case 'URGENT': return 'bg-red-100 text-red-800';
      case 'CRITICAL': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getDeliveryMethodIcon = (method) => {
    switch (method) {
      case 'IN_APP': return <Bell className="w-4 h-4" />;
      case 'EMAIL': return <Mail className="w-4 h-4" />;
      case 'SMS': return <Smartphone className="w-4 h-4" />;
      case 'PUSH_NOTIFICATION': return <Smartphone className="w-4 h-4" />;
      case 'WEBHOOK': return <Globe className="w-4 h-4" />;
      default: return <Bell className="w-4 h-4" />;
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'UNREAD': return <div className="w-2 h-2 bg-blue-500 rounded-full"></div>;
      case 'READ': return <div className="w-2 h-2 bg-gray-400 rounded-full"></div>;
      case 'ARCHIVED': return <div className="w-2 h-2 bg-gray-300 rounded-full"></div>;
      case 'DELETED': return <div className="w-2 h-2 bg-red-400 rounded-full"></div>;
      default: return <div className="w-2 h-2 bg-gray-400 rounded-full"></div>;
    }
  };

  const NotificationItem = ({ notification }) => (
    <div className={`bg-white rounded-lg shadow p-4 hover:shadow-md transition-shadow cursor-pointer ${
      notification.status === 'UNREAD' ? 'border-l-4 border-blue-500' : ''
    }`}>
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <div className="flex items-center space-x-2 mb-2">
            {getTypeIcon(notification.notificationType)}
            <h3 className="text-sm font-semibold text-gray-900">{notification.title}</h3>
            <span className={`px-2 py-1 rounded-full text-xs font-medium ${getPriorityColor(notification.priorityLevel)}`}>
              {notification.priorityLevel}
            </span>
            {notification.actionRequired && (
              <span className="px-2 py-1 rounded-full text-xs font-medium bg-purple-100 text-purple-800">
                Action Required
              </span>
            )}
          </div>
          
          <p className="text-sm text-gray-600 mb-2">{notification.message}</p>
          
          <div className="flex items-center space-x-4 text-xs text-gray-500">
            <div className="flex items-center">
              {getDeliveryMethodIcon(notification.deliveryMethod)}
              <span className="ml-1">{notification.deliveryMethod}</span>
            </div>
            <div className="flex items-center">
              <Clock className="w-3 h-3 mr-1" />
              <span>{new Date(notification.createdAt).toLocaleString()}</span>
            </div>
            {notification.resource && (
              <div className="flex items-center">
                <Users className="w-3 h-3 mr-1" />
                <span>{notification.resource.name}</span>
              </div>
            )}
          </div>
          
          {notification.expiresAt && (
            <div className="text-xs text-orange-600 mt-1">
              Expires: {new Date(notification.expiresAt).toLocaleString()}
            </div>
          )}
        </div>
        
        <div className="flex items-center space-x-2 ml-4">
          {getStatusIcon(notification.status)}
          <div className="flex flex-col space-y-1">
            <button
              onClick={() => handleMarkAsRead(notification.id)}
              className="text-blue-600 hover:text-blue-800"
              title="Mark as read"
            >
              <Check className="w-4 h-4" />
            </button>
            <button
              onClick={() => handleArchive(notification.id)}
              className="text-gray-600 hover:text-gray-800"
              title="Archive"
            >
              <Archive className="w-4 h-4" />
            </button>
            <button
              onClick={() => handleDelete(notification.id)}
              className="text-red-600 hover:text-red-800"
              title="Delete"
            >
              <Trash2 className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
      
      {notification.actionRequired && notification.actionUrl && (
        <div className="mt-3 pt-3 border-t">
          <button
            onClick={() => window.open(notification.actionUrl, '_blank')}
            className="bg-blue-600 text-white px-3 py-1 rounded text-sm hover:bg-blue-700 transition-colors"
          >
            {notification.actionButtonText || 'Take Action'}
          </button>
        </div>
      )}
    </div>
  );

  const handleMarkAsRead = async (id) => {
    try {
      await fetch(`/api/member1/notifications/${id}/mark-read`, {
        method: 'PATCH'
      });
      fetchNotifications();
    } catch (error) {
      console.error('Failed to mark as read:', error);
    }
  };

  const handleArchive = async (id) => {
    try {
      await fetch(`/api/member1/notifications/${id}/archive`, {
        method: 'PATCH'
      });
      fetchNotifications();
    } catch (error) {
      console.error('Failed to archive:', error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this notification?')) {
      try {
        await fetch(`/api/member1/notifications/${id}`, {
          method: 'DELETE'
        });
        fetchNotifications();
      } catch (error) {
        console.error('Failed to delete:', error);
      }
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await fetch('/api/member1/notifications/user/CURRENT_USER/mark-all-read', {
        method: 'PATCH'
      });
      fetchNotifications();
    } catch (error) {
      console.error('Failed to mark all as read:', error);
    }
  };

  const handleDeleteAllRead = async () => {
    if (window.confirm('Are you sure you want to delete all read notifications?')) {
      try {
        await fetch('/api/member1/notifications/user/CURRENT_USER/read', {
          method: 'DELETE'
        });
        fetchNotifications();
      } catch (error) {
        console.error('Failed to delete all read:', error);
      }
    }
  };

  const filteredNotifications = notifications.filter(notification => {
    const matchesSearch = notification.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         notification.message?.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesType = filterType === 'all' || notification.notificationType === filterType;
    const matchesPriority = filterPriority === 'all' || notification.priorityLevel === filterPriority;
    
    const matchesView = viewMode === 'all' ||
                        (viewMode === 'unread' && notification.status === 'UNREAD') ||
                        (viewMode === 'archived' && notification.status === 'ARCHIVED');
    
    return matchesSearch && matchesType && matchesPriority && matchesView;
  });

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
        <h1 className="text-2xl font-bold text-gray-900">Notification Center</h1>
        <div className="flex items-center space-x-3">
          <button
            onClick={() => setShowComposeModal(true)}
            className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
          >
            <Plus className="w-4 h-4 mr-2" />
            Compose
          </button>
          <button className="flex items-center px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors">
            <RefreshCw className="w-4 h-4 mr-2" />
            Refresh
          </button>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-blue-100">
              <Bell className="w-6 h-6 text-blue-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Total</p>
              <p className="text-2xl font-bold text-gray-900">{notifications.length}</p>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-red-100">
              <BellRing className="w-6 h-6 text-red-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Unread</p>
              <p className="text-2xl font-bold text-gray-900">{unreadCount}</p>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-purple-100">
              <AlertTriangle className="w-6 h-6 text-purple-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Action Required</p>
              <p className="text-2xl font-bold text-gray-900">
                {notifications.filter(n => n.actionRequired && n.status === 'UNREAD').length}
              </p>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-orange-100">
              <AlertTriangle className="w-6 h-6 text-orange-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">High Priority</p>
              <p className="text-2xl font-bold text-gray-900">
                {notifications.filter(n => n.priorityLevel === 'HIGH' || n.priorityLevel === 'URGENT').length}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* View Toggle */}
      <div className="flex space-x-2">
        {['all', 'unread', 'archived'].map(view => (
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

      {/* Filters and Search */}
      <div className="bg-white rounded-lg shadow p-6">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
            <input
              type="text"
              placeholder="Search notifications..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          
          <select
            value={filterType}
            onChange={(e) => setFilterType(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="all">All Types</option>
            <option value="BOOKING_CONFIRMED">Booking Confirmed</option>
            <option value="BOOKING_REMINDER">Booking Reminder</option>
            <option value="BOOKING_CANCELLED">Booking Cancelled</option>
            <option value="MAINTENANCE_SCHEDULED">Maintenance Scheduled</option>
            <option value="PAYMENT_DUE">Payment Due</option>
            <option value="SYSTEM_ALERT">System Alert</option>
            <option value="ANNOUNCEMENT">Announcement</option>
          </select>
          
          <select
            value={filterPriority}
            onChange={(e) => setFilterPriority(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="all">All Priorities</option>
            <option value="LOW">Low</option>
            <option value="NORMAL">Normal</option>
            <option value="HIGH">High</option>
            <option value="URGENT">Urgent</option>
            <option value="CRITICAL">Critical</option>
          </select>
          
          <div className="flex space-x-2">
            <button
              onClick={handleMarkAllAsRead}
              className="flex-1 bg-green-600 text-white px-3 py-2 rounded-md hover:bg-green-700 transition-colors text-sm"
            >
              Mark All Read
            </button>
            <button
              onClick={handleDeleteAllRead}
              className="flex-1 bg-red-600 text-white px-3 py-2 rounded-md hover:bg-red-700 transition-colors text-sm"
            >
              Delete Read
            </button>
          </div>
        </div>
      </div>

      {/* Results Count */}
      <div className="text-sm text-gray-600">
        Showing {filteredNotifications.length} of {notifications.length} notifications
      </div>

      {/* Notifications List */}
      <div className="space-y-4">
        {filteredNotifications.map(notification => (
          <NotificationItem key={notification.id} notification={notification} />
        ))}
        
        {filteredNotifications.length === 0 && (
          <div className="text-center py-8">
            <Bell className="w-12 h-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-500">No notifications found</p>
          </div>
        )}
      </div>

      {/* Compose Notification Modal */}
      {showComposeModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-2xl max-h-screen overflow-y-auto">
            <h2 className="text-xl font-bold mb-4">Compose Notification</h2>
            <form className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Notification Type</label>
                <select className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
                  <option value="SYSTEM_ALERT">System Alert</option>
                  <option value="ANNOUNCEMENT">Announcement</option>
                  <option value="MAINTENANCE_SCHEDULED">Maintenance Scheduled</option>
                  <option value="RESOURCE_AVAILABLE">Resource Available</option>
                </select>
              </div>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Title</label>
                <input type="text" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
              </div>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Message</label>
                <textarea className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" rows="4" />
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Priority</label>
                  <select className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
                    <option value="LOW">Low</option>
                    <option value="NORMAL">Normal</option>
                    <option value="HIGH">High</option>
                    <option value="URGENT">Urgent</option>
                    <option value="CRITICAL">Critical</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Delivery Method</label>
                  <select className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
                    <option value="IN_APP">In App</option>
                    <option value="EMAIL">Email</option>
                    <option value="SMS">SMS</option>
                    <option value="PUSH_NOTIFICATION">Push Notification</option>
                  </select>
                </div>
              </div>
              
              <div className="flex items-center space-x-4">
                <label className="flex items-center">
                  <input type="checkbox" className="mr-2" />
                  <span className="text-sm text-gray-700">Action Required</span>
                </label>
                <label className="flex items-center">
                  <input type="checkbox" className="mr-2" />
                  <span className="text-sm text-gray-700">Schedule for later</span>
                </label>
              </div>
              
              <div className="flex space-x-3">
                <button type="button" className="flex-1 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors">
                  <Send className="w-4 h-4 inline mr-2" />
                  Send Notification
                </button>
                <button
                  type="button"
                  onClick={() => setShowComposeModal(false)}
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

export default ResourceNotificationCenter;
