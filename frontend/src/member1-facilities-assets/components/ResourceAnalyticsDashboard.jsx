import React, { useState, useEffect } from 'react';
import { 
  BarChart, Bar, LineChart, Line, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, 
  Tooltip, Legend, ResponsiveContainer 
} from 'recharts';
import { 
  TrendingUp, Users, Calendar, DollarSign, Star, AlertCircle, 
  Filter, Download, RefreshCw, Activity 
} from 'lucide-react';

const ResourceAnalyticsDashboard = () => {
  const [analyticsData, setAnalyticsData] = useState({
    monthlyStats: {},
    weeklyStats: {},
    mostBooked: [],
    highestRated: [],
    highUtilization: []
  });
  const [loading, setLoading] = useState(true);
  const [dateRange, setDateRange] = useState('month');
  const [selectedMetric, setSelectedMetric] = useState('bookings');

  useEffect(() => {
    fetchAnalyticsData();
  }, [dateRange]);

  const fetchAnalyticsData = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/member1/analytics/dashboard');
      const data = await response.json();
      setAnalyticsData(data);
    } catch (error) {
      console.error('Failed to fetch analytics data:', error);
    } finally {
      setLoading(false);
    }
  };

  const COLORS = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6', '#14B8A6'];

  const StatCard = ({ title, value, icon: Icon, trend, color = 'blue' }) => (
    <div className="bg-white rounded-lg shadow p-6">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-gray-600">{title}</p>
          <p className="text-2xl font-bold text-gray-900">{value}</p>
          {trend && (
            <div className={`flex items-center mt-1 text-sm ${
              trend > 0 ? 'text-green-600' : 'text-red-600'
            }`}>
              <TrendingUp className="w-4 h-4 mr-1" />
              {Math.abs(trend)}% from last period
            </div>
          )}
        </div>
        <div className={`p-3 rounded-full bg-${color}-100`}>
          <Icon className={`w-6 h-6 text-${color}-600`} />
        </div>
      </div>
    </div>
  );

  const BookingTrendChart = () => {
    const data = [
      { name: 'Mon', bookings: 12, revenue: 1200 },
      { name: 'Tue', bookings: 15, revenue: 1500 },
      { name: 'Wed', bookings: 18, revenue: 1800 },
      { name: 'Thu', bookings: 14, revenue: 1400 },
      { name: 'Fri', bookings: 20, revenue: 2000 },
      { name: 'Sat', bookings: 8, revenue: 800 },
      { name: 'Sun', bookings: 6, revenue: 600 },
    ];

    return (
      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-lg font-semibold mb-4">Booking Trends</h3>
        <ResponsiveContainer width="100%" height={300}>
          <LineChart data={data}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
            <Legend />
            <Line type="monotone" dataKey="bookings" stroke="#3B82F6" strokeWidth={2} />
            <Line type="monotone" dataKey="revenue" stroke="#10B981" strokeWidth={2} />
          </LineChart>
        </ResponsiveContainer>
      </div>
    );
  };

  const ResourceUtilizationChart = () => {
    const data = [
      { name: 'Main Hall', utilization: 85, bookings: 45 },
      { name: 'Computer Lab', utilization: 72, bookings: 38 },
      { name: 'Library', utilization: 90, bookings: 52 },
      { name: 'Gymnasium', utilization: 68, bookings: 31 },
      { name: 'Meeting Room A', utilization: 78, bookings: 28 },
      { name: 'Study Room', utilization: 92, bookings: 58 },
    ];

    return (
      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-lg font-semibold mb-4">Resource Utilization</h3>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={data}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" angle={-45} textAnchor="end" height={80} />
            <YAxis />
            <Tooltip />
            <Legend />
            <Bar dataKey="utilization" fill="#3B82F6" />
            <Bar dataKey="bookings" fill="#10B981" />
          </BarChart>
        </ResponsiveContainer>
      </div>
    );
  };

  const CategoryDistributionChart = () => {
    const data = [
      { name: 'Classroom', value: 35, color: '#3B82F6' },
      { name: 'Laboratory', value: 25, color: '#10B981' },
      { name: 'Meeting Room', value: 20, color: '#F59E0B' },
      { name: 'Sports Facility', value: 15, color: '#EF4444' },
      { name: 'Study Room', value: 5, color: '#8B5CF6' },
    ];

    return (
      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-lg font-semibold mb-4">Resource Distribution by Category</h3>
        <ResponsiveContainer width="100%" height={300}>
          <PieChart>
            <Pie
              data={data}
              cx="50%"
              cy="50%"
              labelLine={false}
              label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
              outerRadius={80}
              fill="#8884d8"
              dataKey="value"
            >
              {data.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={entry.color} />
              ))}
            </Pie>
            <Tooltip />
          </PieChart>
        </ResponsiveContainer>
      </div>
    );
  };

  const TopResourcesTable = ({ title, resources, type = 'bookings' }) => (
    <div className="bg-white rounded-lg shadow p-6">
      <h3 className="text-lg font-semibold mb-4">{title}</h3>
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Resource
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Category
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                {type === 'bookings' ? 'Bookings' : type === 'rating' ? 'Rating' : 'Utilization'}
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Status
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {resources.slice(0, 5).map((resource, index) => (
              <tr key={index}>
                <td className="px-6 py-4 whitespace-nowrap">
                  <div className="text-sm font-medium text-gray-900">{resource.resource?.name}</div>
                  <div className="text-sm text-gray-500">{resource.resource?.location?.name}</div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className="text-sm text-gray-900">{resource.resource?.category?.name}</span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <div className="text-sm text-gray-900">
                    {type === 'bookings' ? resource.totalBookings : 
                     type === 'rating' ? resource.averageRating?.toFixed(1) : 
                     resource.utilizationRate?.toFixed(1)}%
                  </div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                    resource.resource?.available ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                  }`}>
                    {resource.resource?.available ? 'Available' : 'Unavailable'}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <RefreshCw className="w-8 h-8 animate-spin text-blue-600" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-900">Resource Analytics Dashboard</h1>
        <div className="flex space-x-3">
          <select
            value={dateRange}
            onChange={(e) => setDateRange(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="week">Last Week</option>
            <option value="month">Last Month</option>
            <option value="quarter">Last Quarter</option>
            <option value="year">Last Year</option>
          </select>
          <button
            onClick={fetchAnalyticsData}
            className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
          >
            <RefreshCw className="w-4 h-4 mr-2" />
            Refresh
          </button>
          <button className="flex items-center px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors">
            <Download className="w-4 h-4 mr-2" />
            Export
          </button>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Total Bookings"
          value={analyticsData.monthlyStats?.totalBookings || 0}
          icon={Calendar}
          trend={12}
          color="blue"
        />
        <StatCard
          title="Active Users"
          value={analyticsData.monthlyStats?.totalResources || 0}
          icon={Users}
          trend={8}
          color="green"
        />
        <StatCard
          title="Revenue Generated"
          value={`$${(analyticsData.monthlyStats?.totalRevenue || 0).toLocaleString()}`}
          icon={DollarSign}
          trend={15}
          color="yellow"
        />
        <StatCard
          title="Avg Satisfaction"
          value={`${(analyticsData.monthlyStats?.averageSatisfactionScore || 0).toFixed(1)}/5.0`}
          icon={Star}
          trend={5}
          color="purple"
        />
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <BookingTrendChart />
        <ResourceUtilizationChart />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <CategoryDistributionChart />
        <div className="lg:col-span-2">
          <TopResourcesTable
            title="Most Booked Resources"
            resources={analyticsData.mostBooked}
            type="bookings"
          />
        </div>
      </div>

      {/* Additional Tables */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <TopResourcesTable
          title="Highest Rated Resources"
          resources={analyticsData.highestRated}
          type="rating"
        />
        <TopResourcesTable
          title="High Utilization Resources"
          resources={analyticsData.highUtilization}
          type="utilization"
        />
      </div>
    </div>
  );
};

export default ResourceAnalyticsDashboard;
