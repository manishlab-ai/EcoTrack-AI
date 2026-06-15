# EcoTrack AI — Frontend

> Autonomous Device Recovery & Security Agent  
> Built for the **FAR AWAY Hackathon** · Sentinel-Logic v2.2.0

---

## Project Structure

```
ecotrack-frontend/
├── index.html              ← Single-file working prototype (start here)
├── css/
│   ├── main.css            ← All design tokens, components, layout
│   ├── mobile.css          ← Mobile breakpoints (≤430px, ≤768px)
│   └── desktop.css         ← Desktop sidebar layout (≥1025px)
├── app-js/
│   ├── app.js              ← Core: init, navigation, theme, language, clock
│   ├── map.js              ← Map mode, GPS simulation, BLE proximity
│   ├── auth.js             ← Login, register, forgot password flows
│   ├── settings.js         ← Routines, emergency contacts, OTP validation
│   └── device-tracker.js   ← Secure modal, lock/erase/logout commands
├── pages/
│   ├── login.html          ← Login page reference (content in index.html)
│   ├── active-devices.html ← Active devices reference
│   ├── about.html          ← About page reference
│   └── map-tracker.html    ← Map tracker reference
├── assets/
│   └── icons/              ← Place any custom icons here
└── README.md
```

---

## For the Backend Developer

### How the Frontend Works

This is a **single-page application (SPA)** — all screens live in `index.html` and are shown/hidden via JavaScript. No page reloads. Navigation is handled by `goTo(screenId)` and `navTo(tabName)`.

---

### API Integration Points

All API calls are currently **stubbed with `TODO` comments** inside `<script>` in `index.html` and in `app-js/auth.js`, `app-js/device-tracker.js`, and `app-js/map.js`.

Replace each stub with a real `fetch()` call to your backend.

#### Auth Endpoints

| Method | Endpoint | Request Body | Expected Response |
|--------|----------|--------------|-------------------|
| `POST` | `/auth/login` | `{ email, password }` | `{ userId, token }` |
| `POST` | `/auth/register` | `{ name, email, phone, pwd }` | `{ userId, token }` |
| `POST` | `/auth/new-reg` | `{ org, role, deviceName, email, pwd }` | `{ agentId, token }` |
| `POST` | `/auth/forgot` | `{ email }` | `{ sent: true }` |
| `POST` | `/auth/verify-otp` | `{ email, otp }` | `{ verified: true }` |
| `POST` | `/auth/logout` | `{ token }` | `{ success: true }` |

#### Device Endpoints

| Method | Endpoint | Request Body | Expected Response |
|--------|----------|--------------|-------------------|
| `GET` | `/devices` | — | `[{ id, name, type, status, battery, gsm, gps, color }]` |
| `POST` | `/devices/:id/command` | `{ cmd: 'ring'|'lock'|'erase' }` | `{ success, timestamp }` |
| `DELETE` | `/devices/:id` | — | `{ success: true }` |
| `POST` | `/devices/register` | `{ name, platform, deviceToken }` | `{ deviceId }` |

#### Location Endpoints

| Method | Endpoint | Expected Response | Notes |
|--------|----------|-------------------|-------|
| `GET` | `/location/online` | `{ lat, lon, accuracy }` | Google Find My Device API / Fused Location |
| `GET` | `/location/offline` | `{ rssi, distance, hotCold }` | BLE RSSI proximity |

#### Emergency Contacts Endpoints

| Method | Endpoint | Request Body | Expected Response |
|--------|----------|--------------|-------------------|
| `GET` | `/emergency-contacts` | — | `[{ id, name, email, role, status }]` |
| `POST` | `/emergency-contacts` | `{ email, name }` | `{ id, otp_sent: true }` |
| `POST` | `/emergency-contacts/verify` | `{ id, otp }` | `{ verified: true }` |
| `DELETE` | `/emergency-contacts/:id` | — | `{ success: true }` |

---

### Authentication Token

After login/register, store the `token` in `localStorage` or `sessionStorage`:

```javascript
// After successful login response:
localStorage.setItem('ecotrack_token', response.token);
localStorage.setItem('ecotrack_userId', response.userId);

// On every API call add Authorization header:
headers: {
  'Authorization': `Bearer ${localStorage.getItem('ecotrack_token')}`,
  'Content-Type': 'application/json'
}
```

---

### Where to Integrate in the Frontend

#### Login → `doLogin()` in `app-js/auth.js` (also in `<script>` in `index.html`)

```javascript
// TODO: POST /auth/login → { email, password }
// Replace setTimeout stub with:
const res = await fetch('/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: email.value, password: pass.value })
});
const data = await res.json();
if (data.token) {
  localStorage.setItem('ecotrack_token', data.token);
  navHistory = [];
  goTo('sc-home');
  showToast('Welcome back! 👋', 'success');
}
```

#### Device Commands → `openSecureModal()` in `app-js/device-tracker.js`

After OTP/biometric verification succeeds, `executeAction()` fires.  
Replace the stub inside `executeAction()`:

```javascript
// TODO: POST /devices/:id/command → { cmd: 'ring'|'lock'|'erase' }
await fetch(`/devices/${currentDeviceId}/command`, {
  method: 'POST',
  headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
  body: JSON.stringify({ cmd: pendingAction })
});
```

#### Location Tracking → `triggerFindDevice()` in `app-js/map.js`

```javascript
// TODO: GET /location/online
const loc = await fetch('/location/online', {
  headers: { 'Authorization': `Bearer ${token}` }
}).then(r => r.json());
// Update UI: lat/lon display
```

---

### Screen IDs Reference

| Screen | DOM ID | Triggered by |
|--------|--------|--------------|
| Login | `sc-login` | App start (default) |
| Register | `sc-register` | "Register" link on login |
| New Registration | `sc-newreg` | "New Registration" link |
| Forgot Password | `sc-forgot` | "Forgot password?" link |
| Home | `sc-home` | After login success |
| Device | `sc-device` | Bottom tab — Device |
| Routine | `sc-routine` | Bottom tab — Routine |
| Settings | `sc-settings` | Sidebar → Settings |
| Login/Account (Settings sub) | `sc-loginPage` | Settings → Login |
| Active Devices (Settings sub) | `sc-activeDevices` | Settings → Active Device List |
| Emergency Contacts | `sc-emergency` | Settings → Emergency Contacts |
| About | `sc-about` | Settings → About |
| User Manual | `sc-userManual` | Settings → User Manual |
| Add Routine | `sc-addRoutine` | Routine → Add/Edit |

---

### Key Frontend Functions

| Function | File | Purpose |
|----------|------|---------|
| `doLogin()` | `auth.js` | Handle sign-in form submit |
| `doRegister()` | `auth.js` | Handle register form submit |
| `openSecureModal(action)` | `device-tracker.js` | Open lock/erase/logout confirmation |
| `executeAction()` | `device-tracker.js` | Fire the confirmed command to backend |
| `switchMapMode(mode)` | `map.js` | Switch between 'online' and 'offline' map |
| `triggerFindDevice()` | `map.js` | Start live GPS tracking |
| `renderRoutines()` | `settings.js` | Render routine list from array |
| `renderEmergencyContacts()` | `settings.js` | Render contacts from array |
| `renderActiveDevices()` | `settings.js` | Render device list from array |
| `showToast(msg, type)` | `app.js` | Show notification toast |
| `navTo(tabName)` | `app.js` | Navigate to a screen |
| `goTo(screenId)` | `app.js` | Navigate to screen by DOM ID |
| `goBack()` | `app.js` | Go to previous screen in history |

---

### UI Changes Made (v2.2.1)

1. **Logout / Erase buttons**: Removed from the public login screen. Now only visible inside **Settings → Login (Account)** page — after the user is already authenticated.

2. **Home / Device / Routine navigation**: Moved from the top tab bar to a **bottom navigation bar** on all three main screens. This prevents overlap with language/RTL text at the top and follows standard mobile UX patterns.

---

### Tech Stack (Frontend)

- Pure HTML5 + CSS3 + Vanilla JS (no framework)
- Fonts: Inter + Rajdhani (Google Fonts)
- Icons: Tabler Icons (CDN)
- Theme: Dark / Light mode via CSS `data-theme` attribute
- i18n: 30+ languages via inline translation object `T`
- RTL support: Arabic, Urdu, Hebrew, Farsi via `dir` attribute

---

*For Android native integration, refer to the main EcoTrack AI Android README.*
