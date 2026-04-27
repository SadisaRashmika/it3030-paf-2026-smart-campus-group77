import React, { useState } from 'react';
import { Star, MapPin, Users, Calendar, Wrench, Clock, DollarSign, AlertCircle, CheckCircle, XCircle } from 'lucide-react';

const EnhancedResourceCard = ({ resource, onRate, onBook, onViewDetails, showActions = true }) => {
  const [rating, setRating] = useState(0);
  const [reviewText, setReviewText] = useState('');
  const [showRatingModal, setShowRatingModal] = useState(false);

  const getMaintenanceStatusIcon = (status) => {
    switch (status) {
      case 'GOOD': return <CheckCircle className="w-4 h-4 text-green-500" />;
      case 'NEEDS_ATTENTION': return <AlertCircle className="w-4 h-4 text-yellow-500" />;
      case 'UNDER_MAINTENANCE': return <Wrench className="w-4 h-4 text-orange-500" />;
      case 'OUT_OF_ORDER': return <XCircle className="w-4 h-4 text-red-500" />;
      default: return <CheckCircle className="w-4 h-4 text-gray-500" />;
    }
  };

  const getMaintenanceStatusColor = (status) => {
    switch (status) {
      case 'GOOD': return 'bg-green-100 text-green-800';
      case 'NEEDS_ATTENTION': return 'bg-yellow-100 text-yellow-800';
      case 'UNDER_MAINTENANCE': return 'bg-orange-100 text-orange-800';
      case 'OUT_OF_ORDER': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const renderStars = (rating, interactive = false) => {
    return (
      <div className="flex items-center space-x-1">
        {[1, 2, 3, 4, 5].map((star) => (
          <Star
            key={star}
            className={`w-4 h-4 ${
              star <= rating ? 'text-yellow-400 fill-current' : 'text-gray-300'
            } ${interactive ? 'cursor-pointer hover:text-yellow-400' : ''}`}
            onClick={() => interactive && setRating(star)}
          />
        ))}
        <span className="text-sm text-gray-600 ml-1">
          {rating > 0 ? `${rating}.0` : 'No rating'}
        </span>
        {resource.totalRatings > 0 && (
          <span className="text-xs text-gray-500 ml-1">
            ({resource.totalRatings} reviews)
          </span>
        )}
      </div>
    );
  };

  const handleRatingSubmit = () => {
    if (rating > 0) {
      onRate(resource.id, rating, reviewText);
      setShowRatingModal(false);
      setRating(0);
      setReviewText('');
    }
  };

  return (
    <>
      <div className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-200 overflow-hidden">
        {/* Resource Image */}
        {resource.imageUrl ? (
          <img
            src={resource.imageUrl}
            alt={resource.name}
            className="w-full h-48 object-cover"
          />
        ) : (
          <div className="w-full h-48 bg-gradient-to-br from-blue-100 to-purple-100 flex items-center justify-center">
            <div className="text-center">
              <div className="text-4xl mb-2">{resource.category?.iconName || '📚'}</div>
              <div className="text-gray-600 text-sm">{resource.category?.name || 'Resource'}</div>
            </div>
          </div>
        )}

        <div className="p-4">
          {/* Header */}
          <div className="flex justify-between items-start mb-2">
            <div className="flex-1">
              <h3 className="text-lg font-semibold text-gray-900">{resource.name}</h3>
              <p className="text-sm text-gray-600 line-clamp-2">{resource.description}</p>
            </div>
            <div className="ml-2">
              {getMaintenanceStatusIcon(resource.maintenanceStatus)}
            </div>
          </div>

          {/* Status Badges */}
          <div className="flex flex-wrap gap-2 mb-3">
            <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getMaintenanceStatusColor(resource.maintenanceStatus)}`}>
              {resource.maintenanceStatus?.replace('_', ' ')}
            </span>
            {resource.available ? (
              <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
                Available
              </span>
            ) : (
              <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800">
                Unavailable
              </span>
            )}
            <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
              {resource.type}
            </span>
          </div>

          {/* Rating */}
          <div className="mb-3">
            {renderStars(resource.averageRating || 0)}
          </div>

          {/* Resource Details */}
          <div className="space-y-2 text-sm text-gray-600 mb-4">
            <div className="flex items-center">
              <MapPin className="w-4 h-4 mr-2 text-gray-400" />
              <span>{resource.location?.name}</span>
            </div>
            {resource.maxCapacity && (
              <div className="flex items-center">
                <Users className="w-4 h-4 mr-2 text-gray-400" />
                <span>Capacity: {resource.currentCapacity || 0}/{resource.maxCapacity}</span>
              </div>
            )}
            {resource.bookingCount > 0 && (
              <div className="flex items-center">
                <Calendar className="w-4 h-4 mr-2 text-gray-400" />
                <span>{resource.bookingCount} bookings</span>
              </div>
            )}
            {resource.usageCount > 0 && (
              <div className="flex items-center">
                <Clock className="w-4 h-4 mr-2 text-gray-400" />
                <span>{resource.usageCount} uses</span>
              </div>
            )}
          </div>

          {/* Equipment List */}
          {resource.equipmentList && (
            <div className="mb-4">
              <h4 className="text-sm font-medium text-gray-700 mb-1">Equipment:</h4>
              <div className="flex flex-wrap gap-1">
                {resource.equipmentList.split(',').map((item, index) => (
                  <span key={index} className="text-xs bg-gray-100 text-gray-700 px-2 py-1 rounded">
                    {item.trim()}
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* Actions */}
          {showActions && resource.available && resource.maintenanceStatus === 'GOOD' && (
            <div className="flex space-x-2">
              <button
                onClick={() => onBook(resource)}
                className="flex-1 bg-blue-600 text-white px-3 py-2 rounded-md hover:bg-blue-700 transition-colors text-sm font-medium"
              >
                Book Now
              </button>
              <button
                onClick={() => setShowRatingModal(true)}
                className="flex-1 bg-gray-100 text-gray-700 px-3 py-2 rounded-md hover:bg-gray-200 transition-colors text-sm font-medium"
              >
                Rate
              </button>
              <button
                onClick={() => onViewDetails(resource)}
                className="px-3 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors text-sm font-medium"
              >
                Details
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Rating Modal */}
      {showRatingModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-lg font-semibold mb-4">Rate {resource.name}</h3>
            
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">Your Rating</label>
              {renderStars(rating, true)}
            </div>

            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">Review (Optional)</label>
              <textarea
                value={reviewText}
                onChange={(e) => setReviewText(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                rows="3"
                placeholder="Share your experience..."
              />
            </div>

            <div className="flex space-x-3">
              <button
                onClick={handleRatingSubmit}
                disabled={rating === 0}
                className="flex-1 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
              >
                Submit Rating
              </button>
              <button
                onClick={() => {
                  setShowRatingModal(false);
                  setRating(0);
                  setReviewText('');
                }}
                className="flex-1 bg-gray-100 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-200 transition-colors"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default EnhancedResourceCard;
