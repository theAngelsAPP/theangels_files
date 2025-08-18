const { onDocumentUpdated } = require('firebase-functions/v2/firestore');
const admin = require('firebase-admin');
const axios = require('axios');

if (!admin.apps.length) {
  admin.initializeApp();
}

exports.notifyVolunteerRegistered = onDocumentUpdated('users/{userId}', async event => {
  const beforeData = event.data?.before?.data();
  const afterData = event.data?.after?.data();

  if (!beforeData || !afterData) return;
  if (beforeData.role === 'מתנדב' || afterData.role !== 'מתנדב') return;

  const { userId } = event.params;

  const payload = {
    user_uid: userId,
    firstName: afterData.firstName || '',
    lastName: afterData.lastName || '',
    idNumber: afterData.idNumber || '',
    phone: afterData.Phone || afterData.phone || '',
    email: afterData.Email || afterData.email || '',
    birthDate: afterData.birthDate ? afterData.birthDate.toDate().toISOString() : '',
    city: afterData.city || '',
    role: afterData.role || '',
    volAvailable: afterData.volAvailable || [],
    volCities: afterData.volCities || [],
    volHaveDriverLicense: afterData.volHaveDriverLicense || false,
    volSpecialty: afterData.volSpecialty || [],
    volVerification: afterData.volVerification || ''
  };

  try {
    await axios.post(
      'https://hook.eu2.make.com/9k12oc4to95bfc6vqtg5dm9a14vg87fm',
      payload
    );
  } catch (err) {
    console.error('notifyVolunteerRegistered error:', err);
  }
});
