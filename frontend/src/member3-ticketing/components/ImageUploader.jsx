import { useRef, useState } from "react";
import { ImagePlus, X } from "lucide-react";

export default function ImageUploader({ images, onChange, max = 3 }) {
  const fileInputRef = useRef(null);
  const [dragOver, setDragOver] = useState(false);

  const processFiles = (files) => {
    const remaining = max - images.length;
    const validFiles = Array.from(files).filter(f => f.type.startsWith("image/")).slice(0, remaining);

    validFiles.forEach(file => {
      const reader = new FileReader();
      reader.onload = (e) => {
        onChange(prev => [...prev, { dataUrl: e.target.result, fileName: file.name }]);
      };
      reader.readAsDataURL(file);
    });
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setDragOver(false);
    processFiles(e.dataTransfer.files);
  };

  const handleFileChange = (e) => {
    processFiles(e.target.files);
    e.target.value = "";
  };

  const removeImage = (index) => {
    onChange(prev => prev.filter((_, i) => i !== index));
  };

  return (
    <div className="space-y-3">
      <div
        onDragOver={(e) => { e.preventDefault(); setDragOver(true); }}
        onDragLeave={() => setDragOver(false)}
        onDrop={handleDrop}
        onClick={() => images.length < max && fileInputRef.current?.click()}
        className={`flex cursor-pointer flex-col items-center justify-center rounded-xl border-2 border-dashed p-6 transition-all ${
          dragOver
            ? "border-amber-400 bg-amber-50/60"
            : images.length >= max
              ? "border-slate-200 bg-slate-50 cursor-not-allowed opacity-60"
              : "border-slate-300 bg-white hover:border-amber-300 hover:bg-amber-50/30"
        }`}
      >
        <ImagePlus size={28} className="mb-2 text-slate-400" />
        <p className="text-sm font-semibold text-slate-600">
          {images.length >= max ? `Maximum ${max} images reached` : "Drop images here or click to upload"}
        </p>
        <p className="mt-1 text-xs text-slate-400">{images.length}/{max} images • JPG, PNG, GIF</p>
      </div>

      <input
        ref={fileInputRef}
        type="file"
        accept="image/*"
        multiple
        className="hidden"
        onChange={handleFileChange}
      />

      {images.length > 0 && (
        <div className="flex flex-wrap gap-3">
          {images.map((img, i) => (
            <div key={i} className="group relative h-20 w-20 overflow-hidden rounded-xl border border-slate-200 shadow-sm">
              <img src={img.dataUrl} alt={img.fileName || `Attachment ${i + 1}`} className="h-full w-full object-cover" />
              <button
                type="button"
                onClick={(e) => { e.stopPropagation(); removeImage(i); }}
                className="absolute -right-1 -top-1 flex h-5 w-5 items-center justify-center rounded-full bg-rose-500 text-white opacity-0 shadow transition group-hover:opacity-100"
              >
                <X size={12} />
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
