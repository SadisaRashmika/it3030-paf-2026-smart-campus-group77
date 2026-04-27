import React, { useState, useEffect } from 'react';
import { Star, MessageSquare, ThumbsUp, ThumbsDown, Filter, Search, TrendingUp } from 'lucide-react';

const ResourceRatingSystem = ({ resourceId }) => {
  const [ratings, setRatings] = useState([]);
  const [userRating, setUserRating] = useState(null);
  const [averageRating, setAverageRating] = useState(0);
  const [totalRatings, setTotalRatings] = useState(0);
  const [loading, setLoading] = useState(true);
  const [showReviewForm, setShowReviewForm] = useState(false);
  const [newRating, setNewRating] = useState(0);
  const [newReview, setNewReview] = useState('');
  const [filter, setFilter] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchRatings();
    fetchUserRating();
  }, [resourceId]);

  const fetchRatings = async () => {
    setLoading(true);
    try {
      const response = await fetch(`/api/member1/ratings/resource/${resourceId}`);
      const data = await response.json();
      setRatings(data);
      
      // Calculate average rating
      if (data.length > 0) {
        const avg = data.reduce((sum, rating) => sum + rating.rating, 0) / data.length;
        setAverageRating(avg);
      }
      setTotalRatings(data.length);
    } catch (error) {
      console.error('Failed to fetch ratings:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchUserRating = async () => {
    try {
      const userId = 'CURRENT_USER_ID'; // This should come from auth context
      const response = await fetch(`/api/member1/ratings/user/${userId}/resource/${resourceId}`);
      if (response.ok) {
        const data = await response.json();
        setUserRating(data);
        setNewRating(data.rating);
        setNewReview(data.reviewText || '');
      }
    } catch (error) {
      console.error('Failed to fetch user rating:', error);
    }
  };

  const submitRating = async () => {
    if (newRating === 0) return;

    try {
      const userId = 'CURRENT_USER_ID'; // This should come from auth context
      const response = await fetch('/api/member1/ratings/update-or-create', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          resourceId,
          userId,
          rating: newRating,
          reviewText: newReview,
        }),
      });

      if (response.ok) {
        setShowReviewForm(false);
        fetchRatings();
        fetchUserRating();
      }
    } catch (error) {
      console.error('Failed to submit rating:', error);
    }
  };

  const renderStars = (rating, interactive = false, size = 'normal') => {
    const starSize = size === 'small' ? 'w-3 h-3' : 'w-5 h-5';
    
    return (
      <div className="flex items-center space-x-1">
        {[1, 2, 3, 4, 5].map((star) => (
          <Star
            key={star}
            className={`${starSize} ${
              star <= rating ? 'text-yellow-400 fill-current' : 'text-gray-300'
            } ${interactive ? 'cursor-pointer hover:text-yellow-400' : ''}`}
            onClick={() => interactive && setNewRating(star)}
          />
        ))}
        <span className={`ml-1 ${size === 'small' ? 'text-xs' : 'text-sm'} text-gray-600`}>
          {rating > 0 ? `${rating}.0` : 'No rating'}
        </span>
      </div>
    );
  };

  const getSentimentIcon = (rating) => {
    if (rating >= 4) return <ThumbsUp className="w-4 h-4 text-green-500" />;
    if (rating <= 2) return <ThumbsDown className="w-4 h-4 text-red-500" />;
    return <MessageSquare className="w-4 h-4 text-gray-500" />;
  };

  const filteredRatings = ratings.filter(rating => {
    const matchesSearch = rating.reviewText?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         rating.userId?.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesFilter = filter === 'all' ||
                         (filter === 'positive' && rating.rating >= 4) ||
                         (filter === 'negative' && rating.rating <= 2) ||
                         (filter === 'neutral' && rating.rating === 3);
    
    return matchesSearch && matchesFilter;
  });

  const RatingSummary = () => (
    <div className="bg-white rounded-lg shadow p-6">
      <h3 className="text-lg font-semibold mb-4">Rating Summary</h3>
      
      <div className="flex items-center justify-center mb-6">
        <div className="text-center">
          <div className="text-4xl font-bold text-gray-900">{averageRating.toFixed(1)}</div>
          {renderStars(Math.round(averageRating))}
          <div className="text-sm text-gray-600 mt-1">{totalRatings} reviews</div>
        </div>
      </div>

      <div className="space-y-2">
        {[5, 4, 3, 2, 1].map(star => {
          const count = ratings.filter(r => r.rating === star).length;
          const percentage = totalRatings > 0 ? (count / totalRatings) * 100 : 0;
          
          return (
            <div key={star} className="flex items-center">
              <span className="text-sm text-gray-600 w-8">{star}★</span>
              <div className="flex-1 mx-2">
                <div className="bg-gray-200 rounded-full h-2">
                  <div
                    className="bg-yellow-400 h-2 rounded-full"
                    style={{ width: `${percentage}%` }}
                  />
                </div>
              </div>
              <span className="text-sm text-gray-600 w-12 text-right">{count}</span>
            </div>
          );
        })}
      </div>

      <div className="mt-6 pt-4 border-t">
        <button
          onClick={() => setShowReviewForm(true)}
          className="w-full bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors"
        >
          {userRating ? 'Update Your Review' : 'Write a Review'}
        </button>
      </div>
    </div>
  );

  const ReviewForm = () => (
    <div className="bg-white rounded-lg shadow p-6">
      <h3 className="text-lg font-semibold mb-4">
        {userRating ? 'Update Your Review' : 'Write a Review'}
      </h3>
      
      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700 mb-2">Your Rating</label>
        {renderStars(newRating, true)}
      </div>

      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700 mb-2">Your Review</label>
        <textarea
          value={newReview}
          onChange={(e) => setNewReview(e.target.value)}
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          rows="4"
          placeholder="Share your experience with this resource..."
        />
      </div>

      <div className="flex space-x-3">
        <button
          onClick={submitRating}
          disabled={newRating === 0}
          className="flex-1 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
        >
          {userRating ? 'Update Review' : 'Submit Review'}
        </button>
        <button
          onClick={() => setShowReviewForm(false)}
          className="flex-1 bg-gray-100 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-200 transition-colors"
        >
          Cancel
        </button>
      </div>
    </div>
  );

  const ReviewList = () => (
    <div className="bg-white rounded-lg shadow p-6">
      <div className="flex justify-between items-center mb-4">
        <h3 className="text-lg font-semibold">Reviews ({filteredRatings.length})</h3>
        <div className="flex space-x-2">
          <select
            value={filter}
            onChange={(e) => setFilter(e.target.value)}
            className="px-3 py-1 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="all">All Reviews</option>
            <option value="positive">Positive (4-5★)</option>
            <option value="neutral">Neutral (3★)</option>
            <option value="negative">Negative (1-2★)</option>
          </select>
        </div>
      </div>

      <div className="mb-4">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
          <input
            type="text"
            placeholder="Search reviews..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
      </div>

      <div className="space-y-4 max-h-96 overflow-y-auto">
        {filteredRatings.map(rating => (
          <div key={rating.id} className="border-b pb-4 last:border-b-0">
            <div className="flex justify-between items-start mb-2">
              <div className="flex items-center space-x-2">
                {renderStars(rating.rating, false, 'small')}
                {getSentimentIcon(rating.rating)}
              </div>
              <span className="text-xs text-gray-500">
                {new Date(rating.createdAt).toLocaleDateString()}
              </span>
            </div>
            
            {rating.reviewText && (
              <p className="text-sm text-gray-700 mb-2">{rating.reviewText}</p>
            )}
            
            <div className="text-xs text-gray-500">
              by {rating.userId}
            </div>
          </div>
        ))}
        
        {filteredRatings.length === 0 && (
          <div className="text-center text-gray-500 py-8">
            No reviews found matching your criteria
          </div>
        )}
      </div>
    </div>
  );

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
        <h2 className="text-2xl font-bold text-gray-900">Resource Ratings & Reviews</h2>
        <div className="flex items-center space-x-2">
          <TrendingUp className="w-5 h-5 text-green-500" />
          <span className="text-sm text-gray-600">
            {totalRatings} reviews • {averageRating.toFixed(1)} average
          </span>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-1">
          {showReviewForm ? <ReviewForm /> : <RatingSummary />}
        </div>
        
        <div className="lg:col-span-2">
          <ReviewList />
        </div>
      </div>
    </div>
  );
};

export default ResourceRatingSystem;
