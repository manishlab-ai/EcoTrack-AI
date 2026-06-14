/**
 * EcoTrack Device Tracker
 * Handles device tracking and real-time updates
 */

class DeviceTracker {
    constructor() {
        this.devices = [];
        this.updateInterval = 5000; // 5 seconds
        this.trackingInterval = null;
        this.init();
    }

    /**
     * Initialize the device tracker
     */
    init() {
        console.log('🚀 Initializing DeviceTracker...');
        this.loadDevices();
        this.renderDevices();
        this.updateStats();
        this.startTracking();
    }

    /**
     * Load devices from API or local storage
     */
    loadDevices() {
        // Mock data - Replace with actual API call
        this.devices = [
            {
                id: 'device-001',
                name: 'Sensor A - North Region',
                status: 'active',
                lastUpdate: new Date(),
                latitude: 40.7128,
                longitude: -74.0060,
                battery: 85,
                temperature: 22.5,
                humidity: 65
            },
            {
                id: 'device-002',
                name: 'Sensor B - West Region',
                status: 'active',
                lastUpdate: new Date(),
                latitude: 34.0522,
                longitude: -118.2437,
                battery: 72,
                temperature: 28.3,
                humidity: 45
            },
            {
                id: 'device-003',
                name: 'Sensor C - Central Hub',
                status: 'active',
                lastUpdate: new Date(),
                latitude: 41.8781,
                longitude: -87.6298,
                battery: 58,
                temperature: 20.1,
                humidity: 55
            },
            {
                id: 'device-004',
                name: 'Sensor D - South Station',
                status: 'inactive',
                lastUpdate: new Date(Date.now() - 3600000),
                latitude: 29.7604,
                longitude: -95.3698,
                battery: 10,
                temperature: 25.0,
                humidity: 70
            }
        ];
        console.log(`✅ Loaded ${this.devices.length} devices`);
    }

    /**
     * Render devices to the UI
     */
    renderDevices() {
        const deviceList = document.getElementById('device-list');
        if (!deviceList) return;

        deviceList.innerHTML = '';
        this.devices.forEach(device => {
            const deviceElement = this.createDeviceElement(device);
            deviceList.appendChild(deviceElement);
        });
    }

    /**
     * Create a device element for display
     */
    createDeviceElement(device) {
        const div = document.createElement('div');
        div.className = 'device-item';
        div.innerHTML = `
            <h3>${device.name}</h3>
            <p><strong>ID:</strong> ${device.id}</p>
            <p><strong>Location:</strong> ${device.latitude.toFixed(4)}°N, ${Math.abs(device.longitude).toFixed(4)}°W</p>
            <p><strong>Battery:</strong> ${device.battery.toFixed(1)}%</p>
            <p><strong>Temperature:</strong> ${device.temperature}°C</p>
            <p><strong>Humidity:</strong> ${device.humidity}%</p>
            <p><strong>Last Update:</strong> ${this.formatTime(device.lastUpdate)}</p>
            <span class="status-badge ${device.status}">${device.status.toUpperCase()}</span>
        `;
        return div;
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

        if (seconds < 60) return '🕐 Just now';
        if (minutes < 60) return `⏱️ ${minutes}m ago`;
        if (hours < 24) return `🕰️ ${hours}h ago`;
        return '📅 ' + date.toLocaleDateString();
    }

    /**
     * Start tracking devices
     */
    startTracking() {
        console.log('▶️ Starting device tracking...');
        this.trackingInterval = setInterval(() => {
            this.updateDevices();
        }, this.updateInterval);
    }

    /**
     * Stop tracking devices
     */
    stopTracking() {
        if (this.trackingInterval) {
            clearInterval(this.trackingInterval);
            console.log('⏹️ Device tracking stopped');
        }
    }

    /**
     * Update device information
     */
    updateDevices() {
        this.devices.forEach(device => {
            // Randomly update location slightly
            device.latitude += (Math.random() - 0.5) * 0.001;
            device.longitude += (Math.random() - 0.5) * 0.001;
            device.lastUpdate = new Date();
            
            // Simulate temperature variation
            device.temperature += (Math.random() - 0.5) * 0.5;
            
            // Simulate humidity variation
            device.humidity += (Math.random() - 0.5) * 2;
            device.humidity = Math.max(0, Math.min(100, device.humidity));
            
            // Simulate battery drain
            if (device.status === 'active') {
                device.battery = Math.max(0, device.battery - (Math.random() * 0.5));
            }
        });
        
        this.renderDevices();
        this.updateStats();
    }

    /**
     * Update statistics
     */
    updateStats() {
        const activeDevices = this.getActiveDevices();
        const totalBattery = this.devices.reduce((sum, d) => sum + d.battery, 0);
        const avgBattery = (totalBattery / this.devices.length).toFixed(1);
        
        const activeCountEl = document.getElementById('active-count');
        const totalCountEl = document.getElementById('total-count');
        const batteryAvgEl = document.getElementById('battery-avg');
        
        if (activeCountEl) activeCountEl.textContent = activeDevices.length;
        if (totalCountEl) totalCountEl.textContent = this.devices.length;
        if (batteryAvgEl) batteryAvgEl.textContent = avgBattery + '%';
    }

    /**
     * Get device by ID
     */
    getDevice(deviceId) {
        return this.devices.find(d => d.id === deviceId) || null;
    }

    /**
     * Get all active devices
     */
    getActiveDevices() {
        return this.devices.filter(d => d.status === 'active');
    }

    /**
     * Get all inactive devices
     */
    getInactiveDevices() {
        return this.devices.filter(d => d.status === 'inactive');
    }
}

// Initialize tracker when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        window.deviceTracker = new DeviceTracker();
    });
} else {
    window.deviceTracker = new DeviceTracker();
}
