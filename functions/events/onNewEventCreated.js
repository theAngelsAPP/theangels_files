const { onDocumentCreated } = require('firebase-functions/v2/firestore');
const admin = require('firebase-admin');
const axios = require('axios');

const MAPS_STATIC_API_KEY = process.env.GOOGLE_MAPS_API_KEY || 'GOOGLE_MAPS_API_KEY_PLACEHOLDER';
const NEW_EVENT_WEBHOOK_URL = process.env.MAKE_NEW_EVENT_WEBHOOK_URL || 'MAKE_NEW_EVENT_WEBHOOK_URL_PLACEHOLDER';

if (!admin.apps.length) {
  admin.initializeApp();
}

exports.notifyNewEventCreated = onDocumentCreated('events/{eventId}', async event => {
  const eventData = event.data?.data();
  if (!eventData) return;

  const { eventId } = event.params;
  const {
    eventType = '',
    eventDescription = '',
    eventCity = '',
    eventCreatedBy: creatorId,
    eventTimeStarted,
    eventTimeEnded,
    eventCloseReason,
    eventHandleBy,
    eventLocation,
    eventQuestionChoice,
    eventRating,
    eventRatingText,
    eventStatus,
    eventForm
  } = eventData;

  if (!creatorId) return;

  try {
    const db = admin.firestore();

    // שליפת פרטי המשתמש
    const creatorSnap = await db.collection('users').doc(creatorId).get();
    if (!creatorSnap.exists) return;
    const creator = creatorSnap.data();

    // שליפת איש קשר (אם קיים)
    const contactsQuery = await db.collection('contacts')
      .where('contactUserUID', '==', creatorId)
      .limit(1)
      .get();

    const contact = contactsQuery.empty ? null : contactsQuery.docs[0].data();

    // יצירת Static Map
    let mapImageUrl = '';
    if (eventLocation?.latitude && eventLocation?.longitude) {
      const { latitude, longitude } = eventLocation;
      mapImageUrl = `https://maps.googleapis.com/maps/api/staticmap?center=${latitude},${longitude}&zoom=16&size=572x247&scale=2&language=iw&markers=color:red%7Clabel:%E2%9A%A0%7C${latitude},${longitude}&key=${MAPS_STATIC_API_KEY}`;
    }

    // 🔥 שדות ייעודיים להתראה
    const targetAudience = {
      role: 'volunteer',
      city: creator.city || '',
    };

    const onesignalPayload = {
      title: '📢 אירוע חדש באזור שלך',
      message: `${creator.firstName || 'משתמש'} פתח אירוע מסוג ${eventType}`,
      image: mapImageUrl || '',  // אופציונלי
      eventId: eventId,
      role: 'volunteer',
    };

    // ⚙️ הרכבת ה־Payload
    const payload = {
      // אירוע
      eventId,
      eventType,
      eventDescription,
      eventCity,
      eventCreatedBy: creatorId,
      eventCloseReason,
      eventHandleBy,
      eventQuestionChoice,
      eventRating,
      eventRatingText,
      eventStatus,
      eventForm,
      eventTimeStarted: eventTimeStarted?.toDate().toISOString() || '',
      eventTimeEnded: eventTimeEnded?.toDate().toISOString() || '',
      eventLatitude: eventLocation?.latitude || '',
      eventLongitude: eventLocation?.longitude || '',
      eventMapImageUrl: mapImageUrl,

      // משתמש
      user_firstName: creator.firstName || '',
      user_lastName: creator.lastName || '',
      user_email: creator.email || '',
      user_phone: creator.phone || '',
      user_birthDate: creator.birthDate || '',
      user_city: creator.city || '',
      user_haveGunLicense: creator.hasGunLicense || false,
      user_idNumber: creator.idNumber || '',
      user_imageURL: creator.imageURL || '',
      user_medicalDetails: creator.medicalDetailsAsString || '',
      user_role: creator.role || '',
      user_volAvailable: creator.volAvailableAsString || '',
      user_volCities: creator.volCitiesAsString || '',
      user_volHaveDriverLicense: creator.volHaveDriverLicense || false,
      user_volVerification: creator.volVerification || '',
      user_volSpecialty: creator.volSpecialtyAsString || '',

      // איש קשר (אם קיים)
      emergencyContactName: contact?.contactName || '',
      emergencyContactPhone: contact?.contactPhone || '',
      emergencyContactRelationship: contact?.contactRelationship || '',
      emergencyContactUserUID: contact?.contactUserUID || '',

      // שדות ניהוליים להתראות
      targetAudience,      // <-- 🔵 עבור פילטור ב-Make
      onesignalPayload     // <-- 🔵 עבור שליחה חכמה ל-OneSignal
    };

    // שליחת Webhook
    await axios.post(
      NEW_EVENT_WEBHOOK_URL,
      payload
    );

  } catch (error) {
    console.error('notifyNewEventCreated error:', error);
  }
});
