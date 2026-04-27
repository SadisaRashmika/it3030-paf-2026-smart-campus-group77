import { authService } from '../../services/authService';

const API_BASE = '/api/member1';

class EnhancedResourceService {
  // Resource Categories
  async getCategories() {
    const response = await fetch(`${API_BASE}/categories`, {
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  async createCategory(categoryData) {
    const response = await fetch(`${API_BASE}/categories`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders()
      },
      body: JSON.stringify(categoryData)
    });
    return response.json();
  }

  async updateCategory(id, categoryData) {
    const response = await fetch(`${API_BASE}/categories/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders()
      },
      body: JSON.stringify(categoryData)
    });
    return response.json();
  }

  async deleteCategory(id) {
    const response = await fetch(`${API_BASE}/categories/${id}`, {
      method: 'DELETE',
      headers: authService.getAuthHeaders()
    });
    return response;
  }

  // Resource Locations
  async getLocations() {
    const response = await fetch(`${API_BASE}/locations`, {
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  async createLocation(locationData) {
    const response = await fetch(`${API_BASE}/locations`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders()
      },
      body: JSON.stringify(locationData)
    });
    return response.json();
  }

  async updateLocation(id, locationData) {
    const response = await fetch(`${API_BASE}/locations/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders()
      },
      body: JSON.stringify(locationData)
    });
    return response.json();
  }

  async deleteLocation(id) {
    const response = await fetch(`${API_BASE}/locations/${id}`, {
      method: 'DELETE',
      headers: authService.getAuthHeaders()
    });
    return response;
  }

  // Enhanced Resources
  async getResources(page = 0, size = 20) {
    const response = await fetch(`${API_BASE}/enhanced-resources?page=${page}&size=${size}`, {
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  async getAllResources() {
    const response = await fetch(`${API_BASE}/enhanced-resources/all`, {
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  async getResourceById(id) {
    const response = await fetch(`${API_BASE}/enhanced-resources/${id}`, {
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  async createResource(resourceData) {
    const response = await fetch(`${API_BASE}/enhanced-resources`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders()
      },
      body: JSON.stringify(resourceData)
    });
    return response.json();
  }

  async updateResource(id, resourceData) {
    const response = await fetch(`${API_BASE}/enhanced-resources/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders()
      },
      body: JSON.stringify(resourceData)
    });
    return response.json();
  }

  async deleteResource(id) {
    const response = await fetch(`${API_BASE}/enhanced-resources/${id}`, {
      method: 'DELETE',
      headers: authService.getAuthHeaders()
    });
    return response;
  }

  async searchResources(term, page = 0, size = 20) {
    const response = await fetch(`${API_BASE}/enhanced-resources/search?term=${term}&page=${page}&size=${size}`, {
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  async getResourcesByCategory(categoryId) {
    const response = await fetch(`${API_BASE}/enhanced-resources/category/${categoryId}`, {
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  async getResourcesByLocation(locationId) {
    const response = await fetch(`${API_BASE}/enhanced-resources/location/${locationId}`, {
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  async incrementUsageCount(id) {
    const response = await fetch(`${API_BASE}/enhanced-resources/${id}/increment-usage`, {
      method: 'POST',
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  async incrementBookingCount(id) {
    const response = await fetch(`${API_BASE}/enhanced-resources/${id}/increment-booking`, {
      method: 'POST',
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  // Resource Ratings
  async getRatingsByResourceId(resourceId) {
    const response = await fetch(`${API_BASE}/ratings/resource/${resourceId}`, {
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  async createRating(ratingData) {
    const response = await fetch(`${API_BASE}/ratings`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders()
      },
      body: JSON.stringify(ratingData)
    });
    return response.json();
  }

  async updateOrCreateRating(resourceId, userId, rating, reviewText) {
    const response = await fetch(`${API_BASE}/ratings/update-or-create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders()
      },
      body: JSON.stringify({
        resourceId,
        userId,
        rating,
        reviewText
      })
    });
    return response.json();
  }

  async getUserRatingForResource(resourceId, userId) {
    const response = await fetch(`${API_BASE}/ratings/user/${userId}/resource/${resourceId}`, {
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  async getAverageRatingForResource(resourceId) {
    const response = await fetch(`${API_BASE}/ratings/resource/${resourceId}/average`, {
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  // Resource Maintenance
  async getMaintenanceRecords() {
    const response = await fetch(`${API_BASE}/maintenance`, {
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  async scheduleMaintenance(maintenanceData) {
    const response = await fetch(`${API_BASE}/maintenance`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders()
      },
      body: JSON.stringify(maintenanceData)
    });
    return response.json();
  }

  async startMaintenance(id, technicianId, technicianName) {
    const response = await fetch(`${API_BASE}/maintenance/${id}/start`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders()
      },
      body: JSON.stringify({ technicianId, technicianName })
    });
    return response.json();
  }

  async completeMaintenance(id, notes, cost) {
    const response = await fetch(`${API_BASE}/maintenance/${id}/complete`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders()
      },
      body: JSON.stringify({ notes, cost })
    });
    return response.json();
  }

  async cancelMaintenance(id, reason) {
    const response = await fetch(`${API_BASE}/maintenance/${id}/cancel`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeaders()
      },
      body: JSON.stringify({ reason })
    });
    return response.json();
  }

  // Resource Analytics
  async getDashboardAnalytics() {
    const response = await fetch(`${API_BASE}/analytics/dashboard`, {
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  async getOverallStatistics(startDate, endDate) {
    const response = await fetch(`${API_BASE}/analytics/overall-statistics?startDate=${startDate}&endDate=${endDate}`, {
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  async getResourceStatistics(resourceId, startDate, endDate) {
    const response = await fetch(`${API_BASE}/analytics/resource/${resourceId}/statistics?startDate=${startDate}&endDate=${endDate}`, {
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  // Utility methods
  async seedDefaultData(type) {
    const endpoint = type === 'categories' ? 'categories' : 
                     type === 'locations' ? 'locations' : 'enhanced-resources';
    
    const response = await fetch(`${API_BASE}/${endpoint}/seed`, {
      method: 'POST',
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }

  async getResourceStatistics() {
    const response = await fetch(`${API_BASE}/enhanced-resources/statistics`, {
      headers: authService.getAuthHeaders()
    });
    return response.json();
  }
}

export const enhancedResourceService = new EnhancedResourceService();
export default enhancedResourceService;
