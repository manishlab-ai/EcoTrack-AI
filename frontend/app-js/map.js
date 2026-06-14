/**
 * EcoTrack Map Display
 * Handles map rendering and device visualization
 */

class MapDisplay {
    constructor(containerId = 'map') {
        this.container = document.getElementById(containerId);
        this.devices = [];
        this.selectedDevice = null;
        this.centerLat = 39.8283;
        this.centerLon = -98.5795;
        this.zoomLevel = 1;
        this.init();
    }

    /**
     * Initialize the map display
     */
    init() {
        console.log('🗺️ Initializing Map Display...');
        if (this.container) {
            this.setupMap();
            this.setupEventListeners();
            this.renderDeviceList();
        }
    }

    /**
     * Setup the map
     */
    setupMap() {
        this.container.style.position = 'relative';
        this.container.innerHTML = '<canvas id="map-canvas"></canvas>';
        this.canvas = this.container.querySelector('#map-canvas');
        this.ctx = this.canvas.getContext('2d');
        this.resizeCanvas();
        this.drawMap();
    }

    /**
     * Resize canvas to fit container
     */
    resizeCanvas() {
        this.canvas.width = this.container.offsetWidth;
        this.canvas.height = this.container.offsetHeight;
    }

    /**
     * Draw the map
     */
    drawMap() {
        if (!this.ctx) return;
        
        // Clear canvas
        this.ctx.fillStyle = '#e8edf5';
        this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
        
        // Draw grid
        this.drawGrid();
        
        // Draw title
        this.ctx.fillStyle = '#999';
        this.ctx.font = '14px Arial';
        this.ctx.textAlign = 'center';
        this.ctx.fillText('USA Regional Map - Device Locations', this.canvas.width / 2, 20);
        
        // Draw devices
        this.drawDevices();
    }

    /**
     * Draw grid on map
     */
    drawGrid() {
        const gridSize = 50;
        this.ctx.strokeStyle = '#ddd';
        this.ctx.lineWidth = 0.5;
        
        for (let x = 0; x < this.canvas.width; x += gridSize) {
            this.ctx.beginPath();
            this.ctx.moveTo(x, 30);
            this.ctx.lineTo(x, this.canvas.height);
            this.ctx.stroke();
        }
        
        for (let y = 30; y < this.canvas.height; y += gridSize) {
            this.ctx.beginPath();
            this.ctx.moveTo(0, y);
            this.ctx.lineTo(this.canvas.width, y);
            this.ctx.stroke();
        }
    }

    /**
     * Draw devices on map
     */
    drawDevices() {
        if (!window.deviceTracker) return;
        
        const devices = window.deviceTracker.devices;
        devices.forEach((device, index) => {
            this.drawDevice(device);
        });
    }

    /**
     * Draw a single device on the map
     */
    drawDevice(device) {
        // Convert lat/lon to canvas coordinates (simplified mercator-like projection)
        const x = ((device.longitude + 180) / 360) * this.canvas.width;
        const y = ((90 - device.latitude) / 180) * (this.canvas.height - 30) + 30;
        
        // Draw device marker with glow effect
        const color = device.status === 'active' ? '#667eea' : '#ccc';
        const glowColor = device.status === 'active' ? 'rgba(102, 126, 234, 0.2)' : 'transparent';
        
        // Draw glow
        this.ctx.fillStyle = glowColor;
        this.ctx.beginPath();
        this.ctx.arc(x, y, 15, 0, Math.PI * 2);
        this.ctx.fill();
        
        // Draw device marker
        this.ctx.fillStyle = color;
        this.ctx.beginPath();
        this.ctx.arc(x, y, 8, 0, Math.PI * 2);
        this.ctx.fill();
        
        // Draw border
        this.ctx.strokeStyle = '#fff';
        this.ctx.lineWidth = 2;
        this.ctx.stroke();
        
        // Draw device label
        this.ctx.fillStyle = '#333';
        this.ctx.font = 'bold 11px Arial';
        this.ctx.textAlign = 'center';
        this.ctx.fillText(device.name.split(' - ')[0], x, y + 25);
        
        // Draw battery indicator
        const batteryColor = device.battery > 50 ? '#4CAF50' : 
                            device.battery > 20 ? '#FFC107' : '#F44336';
        this.ctx.fillStyle = batteryColor;
        this.ctx.font = 'bold 10px Arial';
        this.ctx.fillText(`${Math.round(device.battery)}%`, x, y - 15);
    }

    /**
     * Setup event listeners
     */
    setupEventListeners() {
        window.addEventListener('resize', () => {
            this.resizeCanvas();
            this.drawMap();
        });
        
        // Redraw map periodically
        setInterval(() => {
            this.drawMap();
        }, 2000);
    }

    /**
     * Render device list in sidebar
     */
    renderDeviceList() {
        if (!window.deviceTracker) return;
        
        const sidebar = document.getElementById('device-list-sidebar');
        if (!sidebar) return;
        
        sidebar.innerHTML = '';
        window.deviceTracker.devices.forEach(device => {
            const div = document.createElement('div');
            div.className = 'device-item-small';
            div.innerHTML = `<strong>${device.name}</strong><br><small>${device.status.toUpperCase()}</small>`;
            div.style.cursor = 'pointer';
            div.onclick = () => this.selectDevice(device);
            sidebar.appendChild(div);
        });
    }

    /**
     * Select device and show details
     */
    selectDevice(device) {
        this.selectedDevice = device;
        this.showDeviceDetails(device);
    }

    /**
     * Show device details in info panel
     */
    showDeviceDetails(device) {
        const details = document.getElementById('device-details');
        if (!details) return;
        
        details.innerHTML = `
            <p><strong>Name:</strong> ${device.name}</p>
            <p><strong>ID:</strong> ${device.id}</p>
            <p><strong>Status:</strong> <span style="color: ${device.status === 'active' ? '#4CAF50' : '#f44336'};">${device.status.toUpperCase()}</span></p>
            <p><strong>Location:</strong></p>
            <p>&nbsp;&nbsp;${device.latitude.toFixed(4)}°N</p>
            <p>&nbsp;&nbsp;${Math.abs(device.longitude).toFixed(4)}°W</p>
            <p><strong>Battery:</strong> ${device.battery.toFixed(1)}%</p>
            <p><strong>Temperature:</strong> ${device.temperature.toFixed(1)}°C</p>
            <p><strong>Humidity:</strong> ${device.humidity.toFixed(1)}%</p>
            <p><strong>Last Update:</strong> ${this.formatTime(device.lastUpdate)}</p>
        `;
    }

    /**
     * Format time for display
     */
    formatTime(date) {
        const now = new Date();
        const diff = now - date;
        const seconds = Math.floor(diff / 1000);
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);

        if (seconds < 60) return 'Just now';
        if (minutes < 60) return `${minutes}m ago`;
        if (hours < 24) return `${hours}h ago`;
        return date.toLocaleDateString();
    }
}

// Initialize map when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        window.mapDisplay = new MapDisplay('map');
        
        // Update device list when tracker updates
        const updateMapDevices = setInterval(() => {
            if (window.mapDisplay) {
                window.mapDisplay.renderDeviceList();
            }
        }, 5000);
    });
} else {
    window.mapDisplay = new MapDisplay('map');
}
