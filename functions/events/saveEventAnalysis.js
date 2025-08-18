const { onRequest } = require('firebase-functions/v2/https');
const admin = require('firebase-admin');

if (!admin.apps.length) {
  admin.initializeApp();
}

/**
 * שומרת כל מידע שנשלח בגוף הבקשה לתוך מסמך האירוע לפי eventId.
 * כל שדה נוסף בגוף יישמר כשדה חדש תחת האירוע.
 */
exports.saveEventAnalysis = onRequest(async (req, res) => {
  try {
    if (req.method !== 'POST') {
      return res.status(405).json({ error: 'Method Not Allowed. Use POST.' });
    }

    const body = req.body;

    if (!body.eventId) {
      return res.status(400).json({ error: 'Missing eventId in body' });
    }

    const { eventId, ...fieldsToSave } = body;

    const db = admin.firestore();
    const eventRef = db.collection('events').doc(eventId);

    await eventRef.set(fieldsToSave, { merge: true });

    return res.status(200).json({
      success: true,
      message: `Fields saved to event ${eventId}`,
      savedFields: Object.keys(fieldsToSave)
    });

  } catch (error) {
    console.error('saveEventAnalysis error:', error.message);
    return res.status(500).json({ error: error.message });
  }
});