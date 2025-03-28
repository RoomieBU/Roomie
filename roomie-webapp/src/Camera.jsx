// Camera.jsx
import React, { useRef, useState, useEffect } from 'react';

const Camera = ({ onCapture }) => {
  const videoRef = useRef(null); // Reference for the video element
  const canvasRef = useRef(null); // Reference for the canvas element to draw the captured image
  const [image, setImage] = useState(null); // State to store the captured image

  useEffect(() => {
    // Start the camera when the component mounts
    const startCamera = async () => {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({
          video: true, // Only video (no audio)
        });
        if (videoRef.current) {
          videoRef.current.srcObject = stream;
        }
      } catch (err) {
        console.error('Error accessing the camera: ', err);
      }
    };

    startCamera();

    // Cleanup function to stop the camera stream when component unmounts
    return () => {
      if (videoRef.current) {
        const stream = videoRef.current.srcObject;
        const tracks = stream?.getTracks();
        tracks?.forEach(track => track.stop());
      }
    };
  }, []);

  const captureImage = () => {
    const canvas = canvasRef.current;
    const video = videoRef.current;

    // Set canvas size to match the video size
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;

    // Draw the current frame from the video onto the canvas
    const context = canvas.getContext('2d');
    context.drawImage(video, 0, 0, canvas.width, canvas.height);

    // Get the image data URL from the canvas
    const dataUrl = canvas.toDataURL('image/png');
    setImage(dataUrl); // Set the captured image URL to state

    // Call the onCapture callback with the captured image
    if (onCapture) onCapture(dataUrl);
  };

  const uploadImage = () => {
    if (image) {
      // Logic to upload the image (e.g., send to a server)
      console.log('Uploading image:', image);
      // You can send `image` via an API request here
    }
  };

  return (
    <div>
      <h1>Camera Capture</h1>

      {/* Video element to show the live feed */}
      <video ref={videoRef} autoPlay width="100%" />

      <div>
        <button onClick={captureImage}>Capture Image</button>
      </div>

      {/* Canvas element to capture the image */}
      <canvas ref={canvasRef} style={{ display: 'none' }} />

      {/* Display captured image */}
      {image && (
        <div>
          <h2>Captured Image</h2>
          <img src={image} alt="Captured" width="200" />
        </div>
      )}

      {/* Upload button */}
      {image && <button onClick={uploadImage}>Upload Image</button>}
    </div>
  );
};

export default Camera;
