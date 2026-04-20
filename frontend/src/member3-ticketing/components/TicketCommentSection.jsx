import { useState } from "react";
import { Edit3, Trash2, Send, X } from "lucide-react";

export default function TicketCommentSection({ comments, onAdd, onUpdate, onDelete, currentUserEmail }) {
  const [newComment, setNewComment] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [editContent, setEditContent] = useState("");
  const [sending, setSending] = useState(false);

  const handleSend = async () => {
    if (!newComment.trim()) return;
    setSending(true);
    try {
      await onAdd({ content: newComment.trim() });
      setNewComment("");
    } catch {
      // Error handled upstream
    } finally {
      setSending(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const startEdit = (comment) => {
    setEditingId(comment.id);
    setEditContent(comment.content);
  };

  const cancelEdit = () => {
    setEditingId(null);
    setEditContent("");
  };

  const saveEdit = async (commentId) => {
    if (!editContent.trim()) return;
    try {
      await onUpdate(commentId, { content: editContent.trim() });
      cancelEdit();
    } catch {
      // Error handled upstream
    }
  };

  const timeAgo = (dateStr) => {
    if (!dateStr) return "";
    try {
      const now = new Date();
      const date = new Date(dateStr);
      const diff = Math.floor((now - date) / 1000);
      if (diff < 60) return "just now";
      if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
      if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
      return new Date(dateStr).toLocaleDateString();
    } catch { return ""; }
  };

  const isOwner = (comment) => {
    return comment.isOwner || comment.commenterEmail?.toLowerCase() === currentUserEmail?.toLowerCase();
  };

  const getInitials = (email) => {
    if (!email) return "?";
    const name = email.split("@")[0];
    return name.substring(0, 2).toUpperCase();
  };

  return (
    <div className="mt-5 rounded-2xl border border-slate-200 bg-slate-50/40 p-4">
      <h4 className="mb-3 text-xs font-bold uppercase tracking-[0.16em] text-slate-500">
        Comments ({comments.length})
      </h4>

      {/* Comments List */}
      <div className="max-h-72 space-y-3 overflow-y-auto pr-1">
        {comments.length === 0 && (
          <p className="rounded-xl bg-white px-4 py-3 text-center text-sm text-slate-400 shadow-sm">
            No comments yet. Start the conversation!
          </p>
        )}
        {comments.map((comment) => (
          <div key={comment.id} className={`group rounded-xl border p-3 transition ${isOwner(comment) ? "border-amber-100 bg-amber-50/30" : "border-slate-100 bg-white"}`}>
            <div className="flex items-start justify-between gap-2">
              <div className="flex items-center gap-2">
                <span className="flex h-7 w-7 items-center justify-center rounded-full bg-gradient-to-br from-amber-400 to-amber-300 text-[10px] font-bold text-amber-950">
                  {getInitials(comment.commenterEmail)}
                </span>
                <div>
                  <span className="text-xs font-bold text-slate-800">{comment.commenterEmail?.split("@")[0] || "User"}</span>
                  <span className="ml-2 text-[10px] text-slate-400">{timeAgo(comment.createdAt)}</span>
                </div>
              </div>

              {isOwner(comment) && editingId !== comment.id && (
                <div className="flex items-center gap-1 opacity-0 transition group-hover:opacity-100">
                  <button
                    onClick={() => startEdit(comment)}
                    className="flex h-6 w-6 items-center justify-center rounded text-slate-400 transition hover:bg-slate-100 hover:text-slate-700"
                    title="Edit"
                  >
                    <Edit3 size={12} />
                  </button>
                  <button
                    onClick={() => onDelete(comment.id)}
                    className="flex h-6 w-6 items-center justify-center rounded text-slate-400 transition hover:bg-rose-50 hover:text-rose-600"
                    title="Delete"
                  >
                    <Trash2 size={12} />
                  </button>
                </div>
              )}
            </div>

            {editingId === comment.id ? (
              <div className="mt-2">
                <textarea
                  value={editContent}
                  onChange={(e) => setEditContent(e.target.value)}
                  rows={2}
                  className="w-full resize-none rounded-lg border border-amber-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-100"
                />
                <div className="mt-1 flex justify-end gap-2">
                  <button onClick={cancelEdit} className="rounded-lg px-3 py-1 text-xs font-semibold text-slate-500 hover:bg-slate-100">Cancel</button>
                  <button onClick={() => saveEdit(comment.id)} className="rounded-lg bg-amber-400 px-3 py-1 text-xs font-bold text-amber-950 hover:bg-amber-500">Save</button>
                </div>
              </div>
            ) : (
              <p className="mt-1.5 pl-9 text-sm text-slate-700 leading-relaxed">{comment.content}</p>
            )}
          </div>
        ))}
      </div>

      {/* New Comment Input */}
      <div className="mt-3 flex items-end gap-2">
        <textarea
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          onKeyDown={handleKeyDown}
          rows={1}
          placeholder="Type a comment..."
          className="flex-1 resize-none rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-sm transition focus:border-amber-400 focus:outline-none focus:ring-2 focus:ring-amber-100"
        />
        <button
          onClick={handleSend}
          disabled={sending || !newComment.trim()}
          className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-b from-amber-400 to-amber-500 text-amber-950 shadow transition hover:brightness-105 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {sending ? (
            <span className="inline-block h-4 w-4 animate-spin rounded-full border-2 border-amber-950 border-t-transparent" />
          ) : (
            <Send size={16} />
          )}
        </button>
      </div>
    </div>
  );
}
