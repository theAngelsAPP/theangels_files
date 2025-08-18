const { onDocumentCreated, onDocumentUpdated } = require('firebase-functions/v2/firestore');
const admin = require('firebase-admin');
const axios = require('axios');

// ✅ אתחול בטוח
if (!admin.apps.length) {
  admin.initializeApp();
}

exports.notifyVolunteerAssigned = onDocumentUpdated('events/{eventId}', async event => {
  const beforeData = event.data?.before?.data();
  const afterData = event.data?.after?.data();

  if (!beforeData || !afterData) {
    return;
  }

  if (beforeData.eventStatus === afterData.eventStatus || afterData.eventStatus !== 'מתנדב בדרך') {
    return;
  }

  const { eventId } = event.params;
  const eventType = afterData.eventType || '';
  const creatorId = afterData.eventCreatedBy;
  const volunteerId = afterData.eventHandleBy;

  if (!creatorId || !volunteerId) {
    return;
  }

  try {
    const db = admin.firestore();
    const [creatorSnap, volunteerSnap] = await Promise.all([
      db.collection('users').doc(creatorId).get(),
      db.collection('users').doc(volunteerId).get()
    ]);

    if (!creatorSnap.exists || !volunteerSnap.exists) {
      return;
    }

    const creator = creatorSnap.data();
    const volunteer = volunteerSnap.data();

    const payload = {
      event_id: eventId,
      event_type: eventType,
      creator_uid: creatorId,
      creator_firstName: creator.firstName || '',
      volunteer_uid: volunteer.idNumber || '',
      volunteer_firstName: volunteer.firstName || '',
      volunteer_lastName: volunteer.lastName || '',
      volunteer_image_url: volunteer.imageURL || ''
    };

    await axios.post(
      'https://hook.eu2.make.com/woa6w9v3q5u7j0hq2osip0q4eou776v8',
      payload
    );
  } catch (error) {
    console.error('notifyVolunteerAssigned error:', error);
  }
});
