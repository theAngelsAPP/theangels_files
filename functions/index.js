const { notifyVolunteerAssigned } = require('./events/onVolunteerAssigned');
const { notifyNewEventCreated } = require('./events/onNewEventCreated');
const { saveEventAnalysis } = require('./events/saveEventAnalysis');
const { notifyVolunteerRegistered } = require('./users/onVolunteerRegistered');

exports.notifyVolunteerAssigned = notifyVolunteerAssigned;
exports.notifyNewEventCreated = notifyNewEventCreated;
exports.saveEventAnalysis = saveEventAnalysis;
exports.notifyVolunteerRegistered = notifyVolunteerRegistered;
