import React, { useState, useEffect } from 'react';
import { 
  DollarSign, Clock, Calendar, Tag, TrendingUp, TrendingDown, Search, Filter, Plus,
  Edit, Trash2, Eye, RefreshCw, Settings, Percent, CreditCard, AlertCircle,
  CheckCircle, XCircle, Users, Zap, Sun, Moon, Gift, Award, GraduationCap
} from 'lucide-react';

const ResourcePricingManager = () => {
  const [pricing, setPricing] = useState([]);
  const [resources, setResources] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [selectedPricing, setSelectedPricing] = useState(null);
  const [viewMode, setViewMode] = useState('list'); // list, grid, analytics
  const [filterType, setFilterType] = useState('all');
  const [filterCurrency, setFilterCurrency] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState('name');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [pricingRes, resourcesRes] = await Promise.all([
        fetch('/api/member1/pricing'),
        fetch('/api/member1/enhanced-resources/all')
      ]);
      
      const pricingData = await pricingRes.json();
      const resourcesData = await resourcesRes.json();
      
      setPricing(pricingData);
      setResources(resourcesData);
    } catch (error) {
      console.error('Failed to fetch data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getPricingTypeIcon = (type) => {
    switch (type) {
      case 'HOURLY': return <Clock className="w-4 h-4" />;
      case 'DAILY': return <Calendar className="w-4 h-4" />;
      case 'WEEKLY': return <Calendar className="w-4 h-4" />;
      case 'MONTHLY': return <Calendar className="w-4 h-4" />;
      case 'PER_USE': return <DollarSign className="w-4 h-4" />;
      case 'PER_PERSON': return <Users className="w-4 h-4" />;
      case 'PACKAGE': return <Gift className="w-4 h-4" />;
      default: return <Tag className="w-4 h-4" />;
    }
  };

  const getCancellationPolicyColor = (policy) => {
    switch (policy) {
      case 'STRICT': return 'bg-red-100 text-red-800';
      case 'MODERATE': return 'bg-orange-100 text-orange-800';
      case 'STANDARD': return 'bg-yellow-100 text-yellow-800';
      case 'FLEXIBLE': return 'bg-green-100 text-green-800';
      case 'FREE_CANCELLATION': return 'bg-blue-100 text-blue-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const PricingCard = ({ pricing }) => (
    <div className="bg-white rounded-lg shadow p-6 hover:shadow-md transition-shadow">
      <div className="flex justify-between items-start mb-4">
        <div className="flex-1">
          <h3 className="text-lg font-semibold text-gray-900">{pricing.resource?.name || 'Unknown Resource'}</h3>
          <div className="flex items-center space-x-2 mt-1">
            {getPricingTypeIcon(pricing.pricingType)}
            <span className="text-sm text-gray-600">{pricing.pricingType}</span>
            <span className="text-sm text-gray-500">•</span>
            <span className="text-sm text-gray-600">{pricing.currency}</span>
          </div>
        </div>
        <div className="flex items-center space-x-2">
          {pricing.isActive ? (
            <CheckCircle className="w-5 h-5 text-green-500" />
          ) : (
            <XCircle className="w-5 h-5 text-gray-400" />
          )}
          {pricing.depositRequired && (
            <CreditCard className="w-4 h-4 text-purple-500" />
          )}
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4 mb-4">
        <div className="flex items-center justify-between">
          <span className="text-sm text-gray-600">Base Price</span>
          <span className="text-lg font-bold text-gray-900">${pricing.basePrice}</span>
        </div>
        <div className="flex items-center justify-between">
          <span className="text-sm text-gray-600">Per Hour</span>
          <span className="text-sm font-medium text-gray-900">${pricing.pricePerHour || 'N/A'}</span>
        </div>
        <div className="flex items-center justify-between">
          <span className="text-sm text-gray-600">Per Day</span>
          <span className="text-sm font-medium text-gray-900">${pricing.pricePerDay || 'N/A'}</span>
        </div>
        <div className="flex items-center justify-between">
          <span className="text-sm text-gray-600">Per Week</span>
          <span className="text-sm font-medium text-gray-900">${pricing.pricePerWeek || 'N/A'}</span>
        </div>
      </div>

      <div className="flex items-center justify-between mb-4">
        <span className={`px-2 py-1 rounded-full text-xs font-medium ${getCancellationPolicyColor(pricing.cancellationPolicy)}`}>
          {pricing.cancellationPolicy}
        </span>
        {pricing.discountAvailable && (
          <span className="text-xs text-green-600 font-medium flex items-center">
            <Percent className="w-3 h-3 mr-1" />
            Discount Available
          </span>
        )}
      </div>

      {/* Special Pricing Features */}
      <div className="grid grid-cols-2 gap-2 mb-4">
        {pricing.peakHourPricing && (
          <div className="flex items-center text-xs text-gray-600">
            <Zap className="w-3 h-3 mr-1 text-orange-500" />
            Peak Hour
          </div>
        )}
        {pricing.weekendPricing && (
          <div className="flex items-center text-xs text-gray-600">
            <Sun className="w-3 h-3 mr-1 text-blue-500" />
            Weekend
          </div>
        )}
        {pricing.holidayPricing && (
          <div className="flex items-center text-xs text-gray-600">
            <Gift className="w-3 h-3 mr-1 text-red-500" />
            Holiday
          </div>
        )}
        {pricing.memberDiscountAvailable && (
          <div className="flex items-center text-xs text-gray-600">
            <Award className="w-3 h-3 mr-1 text-purple-500" />
            Member
          </div>
        )}
        {pricing.studentDiscountAvailable && (
          <div className="flex items-center text-xs text-gray-600">
            <GraduationCap className="w-3 h-3 mr-1 text-indigo-500" />
            Student
          </div>
        )}
      </div>

      {/* Pricing Details */}
      <div className="space-y-2 mb-4">
        {pricing.minimumBookingHours && (
          <div className="flex justify-between text-xs">
            <span className="text-gray-600">Min Booking</span>
            <span className="text-gray-900">{pricing.minimumBookingHours}h</span>
          </div>
        )}
        {pricing.maximumBookingHours && (
          <div className="flex justify-between text-xs">
            <span className="text-gray-600">Max Booking</span>
            <span className="text-gray-900">{pricing.maximumBookingHours}h</span>
          </div>
        )}
        {pricing.cancellationFeePercentage && (
          <div className="flex justify-between text-xs">
            <span className="text-gray-600">Cancel Fee</span>
            <span className="text-gray-900">{pricing.cancellationFeePercentage}%</span>
          </div>
        )}
        {pricing.lateFeePercentage && (
          <div className="flex justify-between text-xs">
            <span className="text-gray-600">Late Fee</span>
            <span className="text-gray-900">{pricing.lateFeePercentage}%</span>
          </div>
        )}
        {pricing.gracePeriodMinutes && (
          <div className="flex justify-between text-xs">
            <span className="text-gray-600">Grace Period</span>
            <span className="text-gray-900">{pricing.gracePeriodMinutes}min</span>
          </div>
        )}
      </div>

      {/* Effective Period */}
      <div className="mb-4">
        <p className="text-xs text-gray-600">
          <span className="font-medium">Effective:</span>{' '}
          {new Date(pricing.effectiveFrom).toLocaleDateString()} - {' '}
          {pricing.effectiveTo ? new Date(pricing.effectiveTo).toLocaleDateString() : 'Ongoing'}
        </p>
      </div>

      <div className="flex space-x-2">
        <button className="flex-1 bg-blue-600 text-white px-3 py-2 rounded-md hover:bg-blue-700 transition-colors text-sm">
          View Details
        </button>
        <button className="px-3 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors text-sm">
          Edit
        </button>
        <button className="px-3 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors text-sm">
          {pricing.isActive ? 'Deactivate' : 'Activate'}
        </button>
      </div>
    </div>
  );

  const AnalyticsView = () => {
    const totalPricing = pricing.length;
    const activePricing = pricing.filter(p => p.isActive).length;
    const avgPrice = pricing.reduce((sum, p) => sum + p.basePrice, 0) / totalPricing || 0;
    const withDiscount = pricing.filter(p => p.discountAvailable).length;
    const withDeposit = pricing.filter(p => p.depositRequired).length;
    const withPeakHour = pricing.filter(p => p.peakHourPricing).length;
    const withWeekend = pricing.filter(p => p.weekendPricing).length;

    const pricingByType = pricing.reduce((acc, p) => {
      acc[p.pricingType] = (acc[p.pricingType] || 0) + 1;
      return acc;
    }, {});

    const pricingByCurrency = pricing.reduce((acc, p) => {
      acc[p.currency] = (acc[p.currency] || 0) + 1;
      return acc;
    }, {});

    return (
      <div className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold mb-4">Pricing Overview</h3>
            <div className="space-y-3">
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">Total Pricing</span>
                <span className="text-lg font-bold">{totalPricing}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">Active</span>
                <span className="text-lg font-bold text-green-600">{activePricing}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">Avg Price</span>
                <span className="text-lg font-bold">${avgPrice.toFixed(2)}</span>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold mb-4">Discount Features</h3>
            <div className="space-y-3">
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">With Discount</span>
                <span className="text-lg font-bold text-blue-600">{withDiscount}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">With Deposit</span>
                <span className="text-lg font-bold text-purple-600">{withDeposit}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">Peak Hour</span>
                <span className="text-lg font-bold text-orange-600">{withPeakHour}</span>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold mb-4">Time-Based Pricing</h3>
            <div className="space-y-3">
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">Weekend</span>
                <span className="text-lg font-bold text-blue-600">{withWeekend}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">Holiday</span>
                <span className="text-lg font-bold text-red-600">
                  {pricing.filter(p => p.holidayPricing).length}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">Seasonal</span>
                <span className="text-lg font-bold text-green-600">
                  {pricing.filter(p => p.seasonalPricing).length}
                </span>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold mb-4">User Discounts</h3>
            <div className="space-y-3">
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">Member</span>
                <span className="text-lg font-bold text-purple-600">
                  {pricing.filter(p => p.memberDiscountAvailable).length}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">Student</span>
                <span className="text-lg font-bold text-indigo-600">
                  {pricing.filter(p => p.studentDiscountAvailable).length}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">Bulk</span>
                <span className="text-lg font-bold text-green-600">
                  {pricing.filter(p => p.bulkDiscountAvailable).length}
                </span>
              </div>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold mb-4">Pricing by Type</h3>
            <div className="space-y-2">
              {Object.entries(pricingByType).map(([type, count]) => (
                <div key={type} className="flex justify-between">
                  <span className="text-sm text-gray-600">{type}</span>
                  <span className="text-sm font-medium">{count}</span>
                </div>
              ))}
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold mb-4">Pricing by Currency</h3>
            <div className="space-y-2">
              {Object.entries(pricingByCurrency).map(([currency, count]) => (
                <div key={currency} className="flex justify-between">
                  <span className="text-sm text-gray-600">{currency}</span>
                  <span className="text-sm font-medium">{count}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold mb-4">Price Distribution</h3>
          <div className="space-y-4">
            <div>
              <div className="flex justify-between mb-1">
                <span className="text-sm text-gray-600">Low Price ($0-$25)</span>
                <span className="text-sm text-gray-900">
                  {pricing.filter(p => p.basePrice <= 25).length}
                </span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div className="bg-green-500 h-2 rounded-full" style={{ width: '40%' }}></div>
              </div>
            </div>
            <div>
              <div className="flex justify-between mb-1">
                <span className="text-sm text-gray-600">Medium Price ($25-$100)</span>
                <span className="text-sm text-gray-900">
                  {pricing.filter(p => p.basePrice > 25 && p.basePrice <= 100).length}
                </span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div className="bg-yellow-500 h-2 rounded-full" style={{ width: '35%' }}></div>
              </div>
            </div>
            <div>
              <div className="flex justify-between mb-1">
                <span className="text-sm text-gray-600">High Price ($100-$200)</span>
                <span className="text-sm text-gray-900">
                  {pricing.filter(p => p.basePrice > 100 && p.basePrice <= 200).length}
                </span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div className="bg-orange-500 h-2 rounded-full" style={{ width: '20%' }}></div>
              </div>
            </div>
            <div>
              <div className="flex justify-between mb-1">
                <span className="text-sm text-gray-600">Premium Price ($200+)</span>
                <span className="text-sm text-gray-900">
                  {pricing.filter(p => p.basePrice > 200).length}
                </span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div className="bg-red-500 h-2 rounded-full" style={{ width: '5%' }}></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  };

  const filteredPricing = pricing.filter(item => {
    const matchesSearch = item.resource?.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         item.pricingType?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         item.currency?.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesType = filterType === 'all' || item.pricingType === filterType;
    const matchesCurrency = filterCurrency === 'all' || item.currency === filterCurrency;
    
    return matchesSearch && matchesType && matchesCurrency;
  }).sort((a, b) => {
    switch (sortBy) {
      case 'name':
        return a.resource?.name?.localeCompare(b.resource?.name);
      case 'price':
        return a.basePrice - b.basePrice;
      case 'type':
        return a.pricingType?.localeCompare(b.pricingType);
      case 'currency':
        return a.currency?.localeCompare(b.currency);
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
        <h1 className="text-2xl font-bold text-gray-900">Resource Pricing Manager</h1>
        <div className="flex space-x-3">
          <button
            onClick={() => setShowModal(true)}
            className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
          >
            <Plus className="w-4 h-4 mr-2" />
            Add Pricing
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
              <DollarSign className="w-6 h-6 text-blue-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Total Pricing</p>
              <p className="text-2xl font-bold text-gray-900">{pricing.length}</p>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-green-100">
              <CheckCircle className="w-6 h-6 text-green-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Active</p>
              <p className="text-2xl font-bold text-gray-900">
                {pricing.filter(p => p.isActive).length}
              </p>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-purple-100">
              <Percent className="w-6 h-6 text-purple-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">With Discount</p>
              <p className="text-2xl font-bold text-gray-900">
                {pricing.filter(p => p.discountAvailable).length}
              </p>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-orange-100">
              <Zap className="w-6 h-6 text-orange-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Peak Hour</p>
              <p className="text-2xl font-bold text-gray-900">
                {pricing.filter(p => p.peakHourPricing).length}
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
              placeholder="Search pricing..."
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
            <option value="HOURLY">Hourly</option>
            <option value="DAILY">Daily</option>
            <option value="WEEKLY">Weekly</option>
            <option value="MONTHLY">Monthly</option>
            <option value="PER_USE">Per Use</option>
            <option value="PER_PERSON">Per Person</option>
            <option value="PACKAGE">Package</option>
          </select>
          
          <select
            value={filterCurrency}
            onChange={(e) => setFilterCurrency(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="all">All Currencies</option>
            <option value="USD">USD</option>
            <option value="EUR">EUR</option>
            <option value="GBP">GBP</option>
            <option value="JPY">JPY</option>
          </select>
          
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="name">Sort by Name</option>
            <option value="price">Sort by Price</option>
            <option value="type">Sort by Type</option>
            <option value="currency">Sort by Currency</option>
          </select>
        </div>
      </div>

      {/* Results Count */}
      <div className="text-sm text-gray-600">
        Showing {filteredPricing.length} of {pricing.length} pricing configurations
      </div>

      {/* Content */}
      {viewMode === 'analytics' && <AnalyticsView />}

      {viewMode === 'list' && (
        <div className="space-y-4">
          {filteredPricing.map(item => (
            <PricingCard key={item.id} pricing={item} />
          ))}
        </div>
      )}

      {viewMode === 'grid' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredPricing.map(item => (
            <PricingCard key={item.id} pricing={item} />
          ))}
        </div>
      )}

      {/* Add Pricing Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-4xl max-h-screen overflow-y-auto">
            <h2 className="text-xl font-bold mb-4">Add New Pricing Configuration</h2>
            <form className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Resource *</label>
                  <select className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
                    <option>Select Resource</option>
                    {resources.map(resource => (
                      <option key={resource.id} value={resource.id}>{resource.name}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Pricing Type *</label>
                  <select className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
                    <option value="HOURLY">Hourly</option>
                    <option value="DAILY">Daily</option>
                    <option value="WEEKLY">Weekly</option>
                    <option value="MONTHLY">Monthly</option>
                    <option value="PER_USE">Per Use</option>
                    <option value="PER_PERSON">Per Person</option>
                    <option value="PACKAGE">Package</option>
                  </select>
                </div>
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Base Price *</label>
                  <input type="number" step="0.01" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Currency</label>
                  <select className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
                    <option value="USD">USD</option>
                    <option value="EUR">EUR</option>
                    <option value="GBP">GBP</option>
                    <option value="JPY">JPY</option>
                  </select>
                </div>
              </div>
              
              <div className="grid grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Price Per Hour</label>
                  <input type="number" step="0.01" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Price Per Day</label>
                  <input type="number" step="0.01" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Price Per Week</label>
                  <input type="number" step="0.01" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Minimum Booking Hours</label>
                  <input type="number" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Maximum Booking Hours</label>
                  <input type="number" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Cancellation Policy</label>
                  <select className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
                    <option value="STRICT">Strict</option>
                    <option value="MODERATE">Moderate</option>
                    <option value="STANDARD">Standard</option>
                    <option value="FLEXIBLE">Flexible</option>
                    <option value="FREE_CANCELLATION">Free Cancellation</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Cancellation Fee (%)</label>
                  <input type="number" step="0.1" className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
              </div>
              
              <div className="flex items-center space-x-4">
                <label className="flex items-center">
                  <input type="checkbox" className="mr-2" />
                  <span className="text-sm text-gray-700">Deposit Required</span>
                </label>
                <label className="flex items-center">
                  <input type="checkbox" className="mr-2" />
                  <span className="text-sm text-gray-700">Discount Available</span>
                </label>
                <label className="flex items-center">
                  <input type="checkbox" className="mr-2" />
                  <span className="text-sm text-gray-700">Peak Hour Pricing</span>
                </label>
              </div>
              
              <div className="flex space-x-3">
                <button type="button" className="flex-1 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors">
                  Add Pricing
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

export default ResourcePricingManager;
