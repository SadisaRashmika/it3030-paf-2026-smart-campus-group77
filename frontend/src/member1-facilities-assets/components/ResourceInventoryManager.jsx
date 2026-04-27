import React, { useState, useEffect } from 'react';
import { 
  Package, AlertTriangle, TrendingDown, TrendingUp, Search, Filter, Plus,
  Edit, Trash2, Eye, RefreshCw, BarChart3, Clock, DollarSign, Settings,
  ArrowUp, ArrowDown, Calendar, Tag, MapPin, Wrench, AlertCircle
} from 'lucide-react';

const ResourceInventoryManager = () => {
  const [inventory, setInventory] = useState([]);
  const [categories, setCategories] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [locations, setLocations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);
  const [viewMode, setViewMode] = useState('list'); // list, grid, analytics
  const [filterCategory, setFilterCategory] = useState('all');
  const [filterCondition, setFilterCondition] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState('name');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [inventoryRes, categoriesRes, suppliersRes, locationsRes] = await Promise.all([
        fetch('/api/member1/inventory'),
        fetch('/api/member1/inventory/categories'),
        fetch('/api/member1/inventory/suppliers'),
        fetch('/api/member1/inventory/locations')
      ]);
      
      const inventoryData = await inventoryRes.json();
      const categoriesData = await categoriesRes.json();
      const suppliersData = await suppliersRes.json();
      const locationsData = await locationsRes.json();
      
      setInventory(inventoryData);
      setCategories(categoriesData);
      setSuppliers(suppliersData);
      setLocations(locationsData);
    } catch (error) {
      console.error('Failed to fetch data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getConditionColor = (condition) => {
    switch (condition) {
      case 'NEW': return 'bg-green-100 text-green-800';
      case 'GOOD': return 'bg-blue-100 text-blue-800';
      case 'FAIR': return 'bg-yellow-100 text-yellow-800';
      case 'POOR': return 'bg-orange-100 text-orange-800';
      case 'DAMAGED': return 'bg-red-100 text-red-800';
      case 'OBSOLETE': return 'bg-gray-100 text-gray-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getStockStatus = (item) => {
    if (item.currentStock <= item.reorderLevel) {
      return { status: 'critical', color: 'text-red-600', icon: AlertTriangle, label: 'Needs Restock' };
    }
    if (item.currentStock <= item.minimumStock) {
      return { status: 'low', color: 'text-yellow-600', icon: TrendingDown, label: 'Low Stock' };
    }
    if (item.maximumStock && item.currentStock > item.maximumStock) {
      return { status: 'overstock', color: 'text-orange-600', icon: TrendingUp, label: 'Overstock' };
    }
    return { status: 'normal', color: 'text-green-600', icon: Package, label: 'Normal' };
  };

  const InventoryCard = ({ item }) => {
    const stockStatus = getStockStatus(item);
    const StockIcon = stockStatus.icon;
    
    return (
      <div className="bg-white rounded-lg shadow p-6 hover:shadow-md transition-shadow">
        <div className="flex justify-between items-start mb-4">
          <div className="flex-1">
            <h3 className="text-lg font-semibold text-gray-900">{item.itemName}</h3>
            <p className="text-sm text-gray-600 mt-1">{item.itemCode}</p>
            <p className="text-sm text-gray-500 mt-1">{item.description}</p>
            <p className="text-sm text-gray-500">
              Resource: {item.resource?.name || 'Unknown'}
            </p>
          </div>
          <div className="flex items-center space-x-2">
            <StockIcon className={`w-5 h-5 ${stockStatus.color}`} />
            <span className={`px-2 py-1 rounded-full text-xs font-medium ${getConditionColor(item.conditionStatus)}`}>
              {item.conditionStatus}
            </span>
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4 mb-4">
          <div className="flex items-center justify-between">
            <span className="text-sm text-gray-600">Current Stock</span>
            <span className="text-lg font-semibold text-gray-900">{item.currentStock}</span>
          </div>
          <div className="flex items-center justify-between">
            <span className="text-sm text-gray-600">Reorder Level</span>
            <span className="text-sm font-medium text-gray-900">{item.reorderLevel}</span>
          </div>
          <div className="flex items-center justify-between">
            <span className="text-sm text-gray-600">Unit Cost</span>
            <span className="text-sm font-medium text-gray-900">${item.unitCost || 'N/A'}</span>
          </div>
          <div className="flex items-center justify-between">
            <span className="text-sm text-gray-600">Total Value</span>
            <span className="text-sm font-medium text-gray-900">${item.totalValue || 'N/A'}</span>
          </div>
        </div>

        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center space-x-2">
            <span className="text-xs text-gray-500">
              <Tag className="w-3 h-3 inline mr-1" />
              {item.category}
            </span>
            <span className="text-xs text-gray-500">
              <Package className="w-3 h-3 inline mr-1" />
              {item.unitOfMeasure}
            </span>
          </div>
          <span className={`text-xs font-medium ${stockStatus.color}`}>
            {stockStatus.label}
          </span>
        </div>

        {item.supplierName && (
          <div className="mb-4">
            <p className="text-sm text-gray-600">
              <span className="font-medium">Supplier:</span> {item.supplierName}
            </p>
          </div>
        )}

        {item.storageLocation && (
          <div className="mb-4">
            <p className="text-sm text-gray-600">
              <MapPin className="w-3 h-3 inline mr-1" />
              {item.storageLocation}
            </p>
          </div>
        )}

        {item.expiryDate && (
          <div className="mb-4">
            <p className="text-sm text-gray-600">
              <Calendar className="w-3 h-3 inline mr-1" />
              Expires: {new Date(item.expiryDate).toLocaleDateString()}
            </p>
          </div>
        )}

        {item.maintenanceRequired && (
          <div className="mb-4">
            <p className="text-sm text-orange-600">
              <Wrench className="w-3 h-3 inline mr-1" />
              Maintenance Required
            </p>
          </div>
        )}

        <div className="flex space-x-2">
          <button className="flex-1 bg-blue-600 text-white px-3 py-2 rounded-md hover:bg-blue-700 transition-colors text-sm">
            View Details
          </button>
          <button className="px-3 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors text-sm">
            Restock
          </button>
          <button className="px-3 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors text-sm">
            Edit
          </button>
        </div>
      </div>
    );
  };

  const AnalyticsView = () => {
    const totalItems = inventory.length;
    const needsRestock = inventory.filter(item => item.currentStock <= item.reorderLevel).length;
    const lowStock = inventory.filter(item => item.currentStock <= item.minimumStock).length;
    const totalValue = inventory.reduce((sum, item) => sum + (item.totalValue || 0), 0);
    const expiredItems = inventory.filter(item => item.expiryDate && new Date(item.expiryDate) < new Date()).length;
    const maintenanceItems = inventory.filter(item => item.maintenanceRequired).length;

    return (
      <div className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold mb-4">Inventory Overview</h3>
            <div className="space-y-3">
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">Total Items</span>
                <span className="text-lg font-bold">{totalItems}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">Needs Restock</span>
                <span className="text-lg font-bold text-red-600">{needsRestock}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">Low Stock</span>
                <span className="text-lg font-bold text-yellow-600">{lowStock}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">Total Value</span>
                <span className="text-lg font-bold">${totalValue.toFixed(2)}</span>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold mb-4">Alerts</h3>
            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Expired Items</span>
                <span className="text-lg font-bold text-red-600">{expiredItems}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Maintenance Required</span>
                <span className="text-lg font-bold text-orange-600">{maintenanceItems}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Warranty Expiring</span>
                <span className="text-lg font-bold text-yellow-600">0</span>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold mb-4">Top Categories</h3>
            <div className="space-y-2">
              {categories.slice(0, 5).map(category => {
                const count = inventory.filter(item => item.category === category).length;
                return (
                  <div key={category} className="flex justify-between">
                    <span className="text-sm text-gray-600">{category}</span>
                    <span className="text-sm font-medium">{count}</span>
                  </div>
                );
              })}
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold mb-4">Stock Status Distribution</h3>
          <div className="space-y-4">
            <div>
              <div className="flex justify-between mb-1">
                <span className="text-sm text-gray-600">Normal Stock</span>
                <span className="text-sm text-gray-900">
                  {inventory.filter(item => item.currentStock > item.minimumStock && 
                   (!item.maximumStock || item.currentStock <= item.maximumStock)).length}
                </span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div className="bg-green-500 h-2 rounded-full" style={{ width: '60%' }}></div>
              </div>
            </div>
            <div>
              <div className="flex justify-between mb-1">
                <span className="text-sm text-gray-600">Low Stock</span>
                <span className="text-sm text-gray-900">{lowStock}</span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div className="bg-yellow-500 h-2 rounded-full" style={{ width: '25%' }}></div>
              </div>
            </div>
            <div>
              <div className="flex justify-between mb-1">
                <span className="text-sm text-gray-600">Needs Restock</span>
                <span className="text-sm text-gray-900">{needsRestock}</span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div className="bg-red-500 h-2 rounded-full" style={{ width: '15%' }}></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  };

  const filteredInventory = inventory.filter(item => {
    const matchesSearch = item.itemName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         item.itemCode?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         item.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         item.category?.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesCategory = filterCategory === 'all' || item.category === filterCategory;
    const matchesCondition = filterCondition === 'all' || item.conditionStatus === filterCondition;
    
    return matchesSearch && matchesCategory && matchesCondition;
  }).sort((a, b) => {
    switch (sortBy) {
      case 'name':
        return a.itemName?.localeCompare(b.itemName);
      case 'stock':
        return b.currentStock - a.currentStock;
      case 'value':
        return (b.totalValue || 0) - (a.totalValue || 0);
      case 'code':
        return a.itemCode?.localeCompare(b.itemCode);
      default:
        return 0;
    }
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
        <h1 className="text-2xl font-bold text-gray-900">Resource Inventory Manager</h1>
        <div className="flex space-x-3">
          <button
            onClick={() => setShowModal(true)}
            className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
          >
            <Plus className="w-4 h-4 mr-2" />
            Add Item
          </button>
          <button className="flex items-center px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors">
            <RefreshCw className="w-4 h-4 mr-2" />
            Refresh
          </button>
        </div>
      </div>

      {/* Alert Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-red-100">
              <AlertTriangle className="w-6 h-6 text-red-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Needs Restock</p>
              <p className="text-2xl font-bold text-gray-900">
                {inventory.filter(item => item.currentStock <= item.reorderLevel).length}
              </p>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-yellow-100">
              <TrendingDown className="w-6 h-6 text-yellow-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Low Stock</p>
              <p className="text-2xl font-bold text-gray-900">
                {inventory.filter(item => item.currentStock <= item.minimumStock).length}
              </p>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-green-100">
              <Package className="w-6 h-6 text-green-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Total Items</p>
              <p className="text-2xl font-bold text-gray-900">{inventory.length}</p>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-purple-100">
              <DollarSign className="w-6 h-6 text-purple-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Total Value</p>
              <p className="text-2xl font-bold text-gray-900">
                ${inventory.reduce((sum, item) => sum + (item.totalValue || 0), 0).toFixed(0)}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* View Toggle */}
      <div className="flex space-x-2">
        {['list', 'grid', 'analytics'].map(view => (
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
              placeholder="Search inventory..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          
          <select
            value={filterCategory}
            onChange={(e) => setFilterCategory(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="all">All Categories</option>
            {categories.map(category => (
              <option key={category} value={category}>{category}</option>
            ))}
          </select>
          
          <select
            value={filterCondition}
            onChange={(e) => setFilterCondition(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="all">All Conditions</option>
            <option value="NEW">New</option>
            <option value="GOOD">Good</option>
            <option value="FAIR">Fair</option>
            <option value="POOR">Poor</option>
            <option value="DAMAGED">Damaged</option>
            <option value="OBSOLETE">Obsolete</option>
          </select>
          
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="name">Sort by Name</option>
            <option value="code">Sort by Code</option>
            <option value="stock">Sort by Stock</option>
            <option value="value">Sort by Value</option>
          </select>
        </div>
      </div>

      {/* Results Count */}
      <div className="text-sm text-gray-600">
        Showing {filteredInventory.length} of {inventory.length} items
      </div>

      {/* Content */}
      {viewMode === 'analytics' && <AnalyticsView />}

      {viewMode === 'list' && (
        <div className="space-y-4">
          {filteredInventory.map(item => (
            <InventoryCard key={item.id} item={item} />
          ))}
        </div>
      )}

      {viewMode === 'grid' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredInventory.map(item => (
            <InventoryCard key={item.id} item={item} />
          ))}
        </div>
      )}

      {/* Add Item Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-2xl max-h-screen overflow-y-auto">
            <h2 className="text-xl font-bold mb-4">Add New Inventory Item</h2>
            <form className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Item Name *</label>
                  <input type="text" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Item Code *</label>
                  <input type="text" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
              </div>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
                <textarea className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" rows="3" />
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Category *</label>
                  <select className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
                    <option>Select Category</option>
                    {categories.map(category => (
                      <option key={category} value={category}>{category}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Unit of Measure *</label>
                  <input type="text" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
              </div>
              
              <div className="grid grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Current Stock *</label>
                  <input type="number" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Minimum Stock *</label>
                  <input type="number" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Reorder Level *</label>
                  <input type="number" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Unit Cost</label>
                  <input type="number" step="0.01" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Maximum Stock</label>
                  <input type="number" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Supplier Name</label>
                  <input type="text" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Supplier Contact</label>
                  <input type="text" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Storage Location</label>
                  <input type="text" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Condition Status</label>
                  <select className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
                    <option value="NEW">New</option>
                    <option value="GOOD">Good</option>
                    <option value="FAIR">Fair</option>
                    <option value="POOR">Poor</option>
                    <option value="DAMAGED">Damaged</option>
                    <option value="OBSOLETE">Obsolete</option>
                  </select>
                </div>
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Expiry Date</label>
                  <input type="date" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Warranty Expiry</label>
                  <input type="date" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
              </div>
              
              <div className="flex space-x-3">
                <button type="button" className="flex-1 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors">
                  Add Item
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

export default ResourceInventoryManager;
